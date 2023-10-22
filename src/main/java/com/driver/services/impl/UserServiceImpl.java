package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        //create a user of given country. The originalIp of the user should be "countryCode.userId" and return the user. Note that right now user is not connected and thus connected would be false and maskedIp would be null
        //Note that the userId is created automatically by the repository layer
        String countryNameCaps = countryName.toUpperCase();
        if (!countryNameCaps.equals("IND") && !countryNameCaps.equals("USA") && !countryNameCaps.equals("AUS") && !countryNameCaps.equals("CHI") && !countryNameCaps.equals("JPN")) throw new Exception("Country not found");
        Country country = new Country();
        country.setCountryName(CountryName.valueOf(countryNameCaps));
        country.setCode(CountryName.valueOf(countryNameCaps).toCode());

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setOriginalIp(country.getCode()+"."+user.getId());
        country.setUser(user);
        user.setOriginalCountry(country);
        user.setConnected(false);
        userRepository3.save(user);
        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        //subscribe to the serviceProvider by adding it to the list of providers and return updated User
        User user = userRepository3.findById(userId).get();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();

        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);

        serviceProviderRepository3.save(serviceProvider);

        return user;
    }
}