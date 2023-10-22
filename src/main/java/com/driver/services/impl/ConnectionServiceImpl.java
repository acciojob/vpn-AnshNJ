package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user;
        //Check if user exists
        try{
            user = userRepository2.findById(userId).get();
        } catch(Exception e){
            throw new Exception("User not found");
        }

        //check if user is already connected to a SP
        if(user.getConnected()) throw new Exception("Already Connected");

        //Check country
        if(user.getCountry().getCountryName().toString().equals(countryName)) return user;

        //Get SP with given country
        List<ServiceProvider> serviceProviderList = serviceProviderRepository2.findAll();
        int spId = Integer.MAX_VALUE;
        ServiceProvider serviceProvider = null;
        Country country = null;

        for (ServiceProvider currServiceProvider : serviceProviderList) {
            List<Country> countryList = currServiceProvider.getCountryList();
            for (Country currCountry : countryList) {
                if (countryName.equalsIgnoreCase(currCountry.getCountryName().toString()) && spId > currServiceProvider.getId()) {
                    spId = currServiceProvider.getId();
                    serviceProvider = currServiceProvider;
                    country = currCountry;
                }
            }
        }

        if(serviceProvider == null) throw new Exception("Unable to connect");

        //Establish connection
        Connection connection = new Connection();
        connection.setUser(user);
        connection.setServiceProvider(serviceProvider);
        user.getConnectionList().add(connection);

        user.getServiceProviderList().add(serviceProvider);
        user.setMaskedIp(country.getCountryName().toCode() + "." + serviceProvider.getId() + "." + user.getId());
        user.setConnected(true);

        serviceProvider.getUsers().add(user);
        serviceProvider.getConnectionList().add(connection);

        userRepository2.save(user); //Should save both children as well

        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user;
        //Check if user exists
        try{
            user = userRepository2.findById(userId).get();
        } catch(Exception e){
            throw new Exception("User not found");
        }

        if(!user.getConnected()) throw new Exception("Already disconnected");

        user.setConnected(false);
        user.setMaskedIp(null);

        userRepository2.save(user);

        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        //Establish a connection between sender and receiver users
        //To communicate to the receiver, sender should be in the current country of the receiver.
        //If the receiver is connected to a vpn, his current country is the one he is connected to.
        //If the receiver is not connected to vpn, his current country is his original country.
        //The sender is initially not connected to any vpn. If the sender's original country does not match receiver's current country, we need to connect the sender to a suitable vpn. If there are multiple options, connect using the service provider having smallest id
        //If the sender's original country matches receiver's current country, we do not need to do anything as they can communicate. Return the sender as it is.
        //If communication can not be established due to any reason, throw "Cannot establish communication" exception

        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();

        //If receiver is connected
        if(receiver.getConnected()){
            String maskedIp = receiver.getMaskedIp();
            String countryCode = maskedIp.substring(0,3).toUpperCase();

            //If receiver is connected, and their country is the same as senders, return sender directly
            if(countryCode.equals(sender.getCountry().getCountryName().toCode())) return sender;

            //Establish a connection for sender with same country as receiver
            //Get country name from code
            String countryName = "";
            for(CountryName countryName1 : CountryName.values()){
                if(countryName1.toCode().equals(countryCode)){
                    countryName = countryName1.toString();
                }
            }

            sender = connect(senderId , countryName);

            if(!sender.getConnected()) throw new Exception("Cannot establish communication");
            return sender;
        }

        //If receiver is not connected
        //Check if they are in same country
        if(receiver.getCountry().equals(sender.getCountry())) return sender;

        //Establish sender's vpn to receiver's country
        sender = connect(senderId , receiver.getCountry().getCountryName().toString());
        if(!sender.getConnected()) throw new Exception("Cannot establish communication");
        return sender;
    }
}
