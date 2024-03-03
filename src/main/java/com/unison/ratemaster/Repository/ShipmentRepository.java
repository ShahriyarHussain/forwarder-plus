package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.Client;
import com.unison.ratemaster.Entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    @Query("select s from Shipment s order by s.createdOn")
    List<Shipment> getAllShipmentOrderedByCreateDate();
    @Query("select s.masterBl from Shipment s where s.blNo = :blNo")
    byte[] getPdf(@Param("blNo") String blNo);

    @Query("select count(s) from Shipment s where s.invoiceNo = :shipperInvoiceNo and s.shipper = :shipper")
    Integer isShipmentExistsByShipperAndShipperInvoiceNo(@Param("shipperInvoiceNo") String shipperInvoiceNo,
                                                         @Param("shipper") Client shipper);
}
