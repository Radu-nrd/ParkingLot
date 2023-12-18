package com.parking.parkinglot.ejb;

import com.parking.parkinglot.common.CarDto;
import com.parking.parkinglot.entities.Car;
import com.parking.parkinglot.entities.CarPhoto;
import com.parking.parkinglot.common.CarPhotoDto;
import com.parking.parkinglot.entities.User;
import jakarta.ejb.EJBException;
import jakarta.ejb.Stateless;
import jakarta.jws.soap.SOAPBinding;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class CarsBean {
    private static final Logger LOG = Logger.getLogger(CarsBean.class.getName());
    @PersistenceContext
    EntityManager entityManager;

    public List<CarDto> findAllCars(){
        LOG.info("findAllCars");
        try {
            TypedQuery<Car> typedQuerry = entityManager.createQuery("SELECT c FROM Car c",Car.class);
            List<Car> cars = typedQuerry.getResultList();
            return copyCarsToDto(cars);
        }
        catch (Exception ex){
            throw new EJBException(ex);
        }
    }

    private List<CarDto> copyCarsToDto(List<Car>cars){
        List<CarDto> list = new ArrayList<>();
        for(Car car : cars){
            CarDto temp = new CarDto(car.getId(),car.getLicencePlate(),car.getParkingSpot(),car.getUser().getUsername());
            list.add(temp);
        }
        return list;

    }

    public void createCar(String licensePlate,String parkingSpot,Long userId){
        LOG.info("createCar");

        Car car = new Car();
        car.setLicencePlate(licensePlate);
        car.setParkingSpot(parkingSpot);

        User user = entityManager.find(User.class,userId);
        user.getCars().add(car);
        car.setUser(user);

        entityManager.persist(car);
    }

    public void updateCar(Long carId, String licensePlate, String parkingSpot, Long userId){
        LOG.info("updateCar");

        Car car = entityManager.find(Car.class, carId);
        car.setLicencePlate(licensePlate);
        car.setParkingSpot(parkingSpot);

        //rmeove this car from the old owner
        User oldUser = car.getUser();
        oldUser.getCars().remove(car);

        //add the car to its new owner
        User user = entityManager.find(User.class, userId);
        user.getCars().add(car);
        car.setUser(user);
    }

    public CarDto findById(Long id){

        Car car = entityManager.find(Car.class, id);

        return new CarDto(id, car.getLicencePlate(), car.getParkingSpot(), car.getUser().getUsername());
    }

    public void deleteCarsByIds(Collection<Long> carIds){
        for(Long carId:carIds){
            Car car = entityManager.find(Car.class,carId);
            entityManager.remove(car);
        }
    }

    public void addPhotoToCar(Long carId,String filename,String fileType,byte[] fileContent){
        CarPhoto carPhoto = new CarPhoto();
        carPhoto.setFilename(filename);
        carPhoto.setFileType(fileType);
        carPhoto.setFileContent(fileContent);

        Car car = entityManager.find(Car.class,carId);
        if(car.getPhoto()!= null)
            entityManager.remove(car.getPhoto());
        car.setPhoto(carPhoto);
        carPhoto.setCar(car);
        entityManager.persist(carPhoto);

    }
    public CarPhotoDto findPhotoByCarId(Integer carId) {
        List<CarPhoto> photos = entityManager
                .createQuery("SELECT p FROM CarPhoto p where p.car.id = :id", CarPhoto.class)
                .setParameter("id", carId)
                .getResultList();
        if (photos.isEmpty()) {
            return null;
        }
        CarPhoto photo = photos.get(0); // the first element
        return new CarPhotoDto(photo.getId(), photo.getFilename(), photo.getFileType(),
                photo.getFileContent());
    }

}
