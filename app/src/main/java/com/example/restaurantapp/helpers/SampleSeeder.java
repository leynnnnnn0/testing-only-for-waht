package com.example.restaurantapp.helpers;

import com.example.restaurantapp.models.ActivityCard;
import com.example.restaurantapp.models.Item;
import com.example.restaurantapp.models.User;
import java.util.ArrayList;

public final class SampleSeeder {
    public static ArrayList<ActivityCard> activities() {
        ArrayList<ActivityCard> list = new ArrayList<>();
        String[] titles = {"You're invited to Web Development Class",
                "You're invited to Design Workshop",
                "You're invited to Project Presentation"};
        for (int i = 0; i < titles.length; i++) {
            ActivityCard activity = new ActivityCard();
            activity.title = titles[i];
            activity.subtitle = "by Alex Rivera";
            activity.date = "Feb " + (7 - i) + ", 2024";
            activity.time = "6:00 PM – 7:00 PM";
            activity.mode = i == 1 ? "Manila, PH" : "Online";
            list.add(activity);
        }
        return list;
    }

    public static ArrayList<Item> items() {
        ArrayList<Item> list = new ArrayList<>();
        String[] names = {"Cheeseburger", "Double Cheeseburger", "Classic Beef Burger"};
        for (int i = 0; i < names.length; i++) {
            Item item = new Item();
            item.id = i + 1;
            item.name = names[i];
            item.category = "Food";
            item.image = "sample_item_photo";
            item.detail = (320 + i * 100) + " calories";
            item.tag = "Beef";
            item.rating = 4.8;
            item.reviewCount = 120;
            item.location = "Manila, PH";
            item.price = 120 + i * 40;
            item.isAvailable = 1;
            item.description = item.name + " with beef, cheese, lettuce, and tomato. Preview data only.";
            list.add(item);
        }
        String[] seatCategories = {"3 Seaters", "5 Seaters", "6 Seaters", "8 Seaters"};
        String[] vehicleNames = {"City Compact", "Family Sedan", "Six-seat SUV", "Passenger Van"};
        String[] vehicleImages = {"personal_seats_3", "personal_seats_5",
                "personal_seats_6", "personal_seats_8"};
        String[] vehicleDescriptions = {
                "Compact, comfortable, and easy to navigate through the city. A practical choice for everyday trips.",
                "A comfortable sedan with room for family and friends on daily drives or weekend outings.",
                "Spacious seating and a smooth ride make this SUV a flexible choice for group trips.",
                "Bring everyone along with generous seating and space for longer journeys."
        };
        for (int i = 0; i < seatCategories.length; i++) {
            Item item = new Item();
            item.id = names.length + i + 1;
            item.name = vehicleNames[i];
            item.category = seatCategories[i];
            item.image = vehicleImages[i];
            item.detail = seatCategories[i];
            item.tag = "Vehicle";
            item.rating = 4.8;
            item.reviewCount = 24;
            item.location = "Manila, PH";
            item.price = 1200 + i * 450;
            item.isAvailable = 1;
            item.description = vehicleDescriptions[i];
            list.add(item);
        }
        return list;
    }

    public static ArrayList<User> persons() {
        ArrayList<User> list = new ArrayList<>();
        String[] names = {"Jamie Reyes", "Alex Rivera", "Morgan Lee"};
        String[] roles = {"Instructor", "Customer", "Admin"};
        String[] specialties = {"Training", "Member", "Operations"};
        String[] genders = {"Female", "Male", "Female"};
        for (int i = 0; i < names.length; i++) {
            User user = new User();
            user.id = 101 + i;
            user.fullName = names[i];
            user.role = roles[i];
            user.specialty = specialties[i];
            user.gender = genders[i];
            user.age = 27 + i;
            user.creditPoints = 250 - i * 50;
            user.isActive = i == 2 ? 0 : 1;
            user.phone = "0917 000 000" + (i + 1);
            user.email = names[i].toLowerCase().replace(' ', '.') + "@example.com";
            user.password = "1234";
            user.location = "Manila, PH";
            user.joinedDate = "Oct " + (14 - i) + ", 2026";
            list.add(user);
        }
        return list;
    }
}
