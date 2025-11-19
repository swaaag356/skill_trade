package com.itis.oris.service;

import com.itis.oris.repository.DataRepository;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DataService {
    private final DataRepository dataRepository;

    public DataService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void getAttributes(HttpServletRequest req) {
        req.setAttribute("countOfActiveOffers", dataRepository.getActiveOffersCount());
        req.setAttribute("countOfDidOffers", dataRepository.getCompletedTradesCount());
        req.setAttribute("userCount",  dataRepository.getUsersCount());
    }
}
