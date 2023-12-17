package com.parking.parkinglot.ejb;

import com.parking.parkinglot.common.CarDto;
import com.parking.parkinglot.common.UserDto;
import com.parking.parkinglot.entities.Car;
import com.parking.parkinglot.entities.User;
import com.parking.parkinglot.entities.UserGroup;
import jakarta.ejb.EJBException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jws.soap.SOAPBinding;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class UserBean {
    private static final Logger LOG = Logger.getLogger(UserBean.class.getName());
    @PersistenceContext
    EntityManager entityManager;

    public List<UserDto> findAllUsers(){
        LOG.info("findAllCars");
        try {
            TypedQuery<User> typedQuerry = entityManager.createQuery("SELECT u FROM User u", User.class);
            List<User> users = typedQuerry.getResultList();
            return copyUsersToDto(users);
        }
        catch (Exception ex){
            throw new EJBException(ex);
        }
    }

    private List<UserDto> copyUsersToDto(List<User>users){
        List<UserDto> list = new ArrayList<>();
        for(User user : users){
            UserDto temp = new UserDto(user.getId(),user.getUsername(),user.getPassword(),user.getEmail());
            list.add(temp);
        }
        return list;

    }

    @Inject
    PasswordBean passwordBean;
    public void createUser(String username, String email, String password, Collection<String> groups){
        LOG.info("createUser");

        User newUser=new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordBean.convertToSha256(password));
        entityManager.persist(newUser);

        assignGroupsToUser(username, groups);
    }
    private void assignGroupsToUser(String username, Collection<String>groups){
        LOG.info("assignGroupsToUser");

        for(String group:groups) {
            UserGroup userGroup=new UserGroup();
            userGroup.setUsername(username);
            userGroup.setUserGroup(group);
            entityManager.persist(userGroup);
        }
    }


}
