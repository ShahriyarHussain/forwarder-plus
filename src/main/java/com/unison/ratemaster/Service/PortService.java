package com.unison.ratemaster.Service;

import com.unison.ratemaster.Entity.Port;
import com.unison.ratemaster.Repository.PortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortService {

    private final PortRepository portRepository;

    public List<Port> getPorts() {
        return portRepository.findAll();
    }
}
