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
        boolean isPresent = false;
        countryName = countryName.toUpperCase();

        for(CountryName country : CountryName.values()){
            if(country.equals(countryName)) isPresent = true;
        }

        if(!isPresent) throw new Exception("Country not found");

        Country country = new Country();
        country.setCountryName(CountryName.valueOf(countryName));
        country.setCode(CountryName.valueOf(countryName).toCode());


        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setOriginalCountry(country);
        user.setOriginalIp(country.getCode()+"."+user.getId());
        country.setUser(user);

        userRepository3.save(user);


        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) throws Exception {
        User user;
        try{
            user = userRepository3.findById(userId).get();
        } catch(Exception e){
            throw new Exception("User not found");
        }

        ServiceProvider serviceProvider;
        try{
            serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();
        } catch(Exception e){
            throw new Exception("ServiceProvider not found");
        }

        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);

        serviceProviderRepository3.save(serviceProvider);

        return user;
    }
}
