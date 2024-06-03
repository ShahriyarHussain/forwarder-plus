package com.unison.ratemaster.View.Misc;


import com.unison.ratemaster.Entity.Port;
import com.unison.ratemaster.Entity.Schedule;
import com.unison.ratemaster.Entity.Transshipment;
import com.unison.ratemaster.Service.ScheduleService;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@PageTitle("View Schedules")
@Route(value = "view-schedules", layout = MainView.class)
public class ScheduleView extends VerticalLayout{

    public ScheduleView(@Autowired ScheduleService scheduleService) {
        H3 title = new H3("View Schedules");
        List<Schedule> scheduleList = scheduleService.getAllSchedules();

        Grid<Schedule> grid = new Grid<>();
        grid.setMinHeight(35, Unit.EM);

        grid.addColumn(Schedule::getCarrier)
                .setSortable(false).setAutoWidth(true).setResizable(true).setHeader("Carrier");
        grid.addColumn(schedule -> getPortIfExists(schedule.getPortOfLoading()))
                .setAutoWidth(true).setResizable(true).setHeader("Loading Port");
        grid.addColumn(schedule -> schedule.getLoadingPortEtd().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")))
                .setSortable(true).setComparator(Schedule::getLoadingPortEtd).setAutoWidth(true).setResizable(true).setHeader("ETD POL");
        grid.addColumn(schedule -> getPortIfExists(schedule.getPortOfDestination()))
                .setAutoWidth(true).setResizable(true).setHeader("Destination Port");
        grid.addColumn(schedule -> schedule.getDestinationPortEta().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")))
                .setSortable(true).setComparator(Schedule::getDestinationPortEta).setAutoWidth(true).setResizable(true).setHeader("Destination ETA");

        grid.setRowsDraggable(true);
        grid.setItems(scheduleList);
        grid.setItemDetailsRenderer(createPersonDetailsRenderer());

        add(title, grid);

    }

    private String getPortIfExists(Port port) {
        if (port == null) return "N/A";
        return port.getPortName() + ", " + port.getPortCountry();
    }

    private static ComponentRenderer<TransShipmentDetails, Schedule> createPersonDetailsRenderer() {
        return new ComponentRenderer<>(TransShipmentDetails::new,
                TransShipmentDetails::setTransShipment);
    }

    private static class TransShipmentDetails extends VerticalLayout {
        Grid<Transshipment> grid = new Grid<>();

        public TransShipmentDetails() {
            grid.addColumn(Transshipment::getVesselName).setHeader("Vessel Name");
            grid.addColumn(transshipment -> transshipment.getPort().getPortName()).setHeader("Port Name");
            grid.addColumn(transshipment -> Util.formatDateTime(Util.GENERIC_DATE_PATTERN, transshipment.getPortArrival()))
                    .setHeader("ETA");
            add(grid);
        }


        public void setTransShipment(Schedule schedule) {
            List<Transshipment> transShipments = new ArrayList<>(schedule.getTransshipment());
            grid.setMaxHeight(transShipments.size() * 5, Unit.EM);
            transShipments.sort(Comparator.comparing(Transshipment::getPortArrival));
            grid.setItems(transShipments);
        }
    }

}


