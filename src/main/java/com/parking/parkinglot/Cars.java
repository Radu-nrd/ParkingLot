package com.parking.parkinglot;

import com.parking.parkinglot.common.CarDto;
import com.parking.parkinglot.ejb.CarsBean;
import jakarta.annotation.security.DeclareRoles;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpMethodConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.annotation.HttpConstraint;

@DeclareRoles({"READ_CARS", "WRITE_CARS"})
@ServletSecurity(value = @HttpConstraint(rolesAllowed = {"READ_CARS"}),
        httpMethodConstraints = {@HttpMethodConstraint(value = "POST", rolesAllowed =
                {"WRITE_CARS"})})
@WebServlet(name = "Cars", value = "/Cars")
public class Cars extends HttpServlet {

    @Inject
    CarsBean carsBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<CarDto> cars=carsBean.findAllCars();
        request.setAttribute("cars",cars);
        request.getRequestDispatcher("/WEB-INF/pages/pages.jsp").forward(request,response);
        request.setAttribute("numberOfFreeParkingSpots", 10);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] carIdsAsString = request.getParameterValues("car_ids");
        if(carIdsAsString!=null){
            List<Long>carIds = new ArrayList<>();
            for(String carIdString : carIdsAsString){
                carIds.add(Long.parseLong(carIdString));
            }
            carsBean.deleteCarsByIds(carIds);
        }
        response.sendRedirect(request.getContextPath()+"/Cars");
    }
}