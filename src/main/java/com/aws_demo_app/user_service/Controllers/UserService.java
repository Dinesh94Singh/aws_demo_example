package com.aws_demo_app.user_service.Controllers;

import com.aws_demo_app.user_service.DTO.User;
import com.aws_demo_app.user_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="v1/user-service/")
public class UserService {

    @Autowired
    private UserRepository userRepository; // field injection

    @RequestMapping(value="addUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public com.aws_demo_app.user_service.Repository.DAO.User createUser(@RequestBody User userDetails) throws ParseException {
        final com.aws_demo_app.user_service.Repository.DAO.User user = new com.aws_demo_app.user_service.Repository.DAO.User();

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());

        // TODO: add password salt to make the password storing more secure

        user.setPassword(userDetails.getPassword());
        user.setPasswordHint(userDetails.getPasswordHint());

        Date dob = new SimpleDateFormat("dd/MM/yyyy").parse(userDetails.getDob());
        user.setDateOfBirth(dob);

        userRepository.save(user);

        return user;
    }

    @RequestMapping(value="getUsers", method = RequestMethod.GET)
    @ResponseBody
    public List<com.aws_demo_app.user_service.Repository.DAO.User> getUsers() {
        // TODO: This should be converted to DTO layer and send it back as response body instead of DAO layer
        final List<com.aws_demo_app.user_service.Repository.DAO.User> result = new ArrayList<>();
        final Iterable<com.aws_demo_app.user_service.Repository.DAO.User> iterable = userRepository.findAll();

        iterable.forEach(result::add);
        return result;
    }
}
