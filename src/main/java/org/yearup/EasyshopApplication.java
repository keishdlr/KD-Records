package org.yearup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EasyshopApplication
{

    public static void main(String[] args) {

        if(args.length != 2){
            System.exit(0);
        }else{
            System.setProperty("dbUsername", args[0]);
            System.setProperty("dbPassword", args[1]);
            SpringApplication.run(EasyshopApplication.class, args);
        }

    }

}
