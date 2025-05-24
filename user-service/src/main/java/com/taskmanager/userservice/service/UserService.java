package com.taskmanager.userservice.service;


import com.taskmanager.userservice.repository.UserRepository;
import com.taskmanager.userservice.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Value("${jwt.secret}")
    private String jwtSecret;

    public User registerUser(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setRole("EMPLOYEE");
//        User savedUser = userRepository.save(user);
//        redisTemplate.opsForValue().set("user:" + user.getUsername(), savedUser);
//        return savedUser;

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole());
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public String loginUser(User user) {
//        User cachedUser = (User) redisTemplate.opsForValue().get("user:" + user.getUsername());
//        if (cachedUser == null) {
//            cachedUser = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
//            redisTemplate.opsForValue().set("user:" + user.getUsername(), cachedUser);
//        }
//        if (passwordEncoder.matches(user.getPassword(), cachedUser.getPassword())) {
//            return Jwts.builder()
//                    .setSubject(user.getUsername())
//                    .claim("role", cachedUser.getRole())
//                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
//                    .compact();
//        }
//        throw new RuntimeException("Invalid credentials");
        User dbUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return Jwts.builder()
                    .setSubject(user.getUsername())
                    .claim("role", dbUser.getRole())
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();
        }
        throw new RuntimeException("Invalid credentials");
    }
}