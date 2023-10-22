package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) throws Exception {
        Admin admin;
        try{
            admin = adminRepository1.findById(adminId).get();
        } catch(Exception e){
            throw new Exception("Admin not found");
        }

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);

        admin.getServiceProviders().add(serviceProvider);

        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        ServiceProvider serviceProvider;
        try{
            serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();
        } catch(Exception e){
            throw new Exception("Service Provider Not Found");
        }

        //check if present
        boolean isPresent = false;
        countryName = countryName.toUpperCase();

        for(CountryName country : CountryName.values()){
            if(country.equals(countryName)) isPresent = true;
        }

        if(!isPresent) throw new Exception("Country not found");

        //make new country
        Country country = new Country();
        country.setCountryName(CountryName.valueOf(countryName));
        country.setCode(CountryName.valueOf(countryName).toCode());


        serviceProvider.getCountryList().add(country);
        country.setServiceProvider(serviceProvider);

        return serviceProvider;
    }
}
