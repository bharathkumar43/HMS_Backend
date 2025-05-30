package com.excelr.controller;
//
//import com.excelr.model.Room;
import com.excelr.model.User;
//import com.excelr.repo.RoomRepository;
import com.excelr.repo.UserRepo;
import com.excelr.util.JwtUtil;

import java.util.HashMap;
//import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
//@CrossOrigin(origins = "*") // Optional: Allows requests from any frontend
@RequestMapping
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        Optional<User> existingUser = userRepo.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Username already exists");
            return ResponseEntity.status(409).body(response); // 409 Conflict
        }

        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful");
        return ResponseEntity.ok(response);
    }


    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Optional<User> user = userRepo.findByUsername(username);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            Map<String, String> response = new HashMap<>();
            String token = jwtUtil.generateToken(username);

            response.put("login", "success");
            response.put("token", token);
            response.put("role", user.get().getRole());
            response.put("username", user.get().getUsername());
            response.put("id", String.valueOf(user.get().getId()));

            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response1 = new HashMap<>();
            response1.put("login", "fail");
            return ResponseEntity.status(401).body(response1);
        }
    }
//   //rooms 
//    @Autowired
//    private RoomRepository roomRepo;
//
//    // Create a new room
//    @PostMapping("/rooms")
//    public ResponseEntity<?> createRoom(@RequestBody Room room) {
//        roomRepo.save(room);
//        return ResponseEntity.ok("Room Created Successfully");
//    }
//
//    // Get all rooms
//    @GetMapping("/rooms")
//    public List<Room> getAllRooms() {
//        return roomRepo.findAll();
//    }
//
//    // Get a room by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getRoomById(@PathVariable Long id) {
//        Optional<Room> optionalRoom = roomRepo.findById(id);
//        return optionalRoom.map(ResponseEntity::ok)
//                           .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    // Update a room
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody Room room) {
//        Optional<Room> optionalRoom = roomRepo.findById(id);
//        if (optionalRoom.isPresent()) {
//            Room existingRoom = optionalRoom.get();
//            existingRoom.setRoomNumber(room.getRoomNumber());
//            existingRoom.setCapacity(room.getCapacity());
//            existingRoom.setRoomType(room.getRoomType());
//            existingRoom.setPicture(room.getPicture());
//            roomRepo.save(existingRoom);
//            return ResponseEntity.ok("Room Updated Successfully");
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    // Delete a room
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
//        if (roomRepo.existsById(id)) {
//            roomRepo.deleteById(id);
//            return ResponseEntity.ok("Room Deleted Successfully");
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//     
    
}
