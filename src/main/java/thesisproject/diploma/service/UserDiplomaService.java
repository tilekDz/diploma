package thesisproject.diploma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import thesisproject.diploma.entity.Role;
import thesisproject.diploma.entity.UserDiploma;
import thesisproject.diploma.repository.RoleRepository;
import thesisproject.diploma.repository.UserDiplomaRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@Service
public class UserDiplomaService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDiplomaRepository userDiplomaRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDiploma saveUser(UserDiploma user, String role) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user.setDate(new Date());
        Role userRole = roleRepository.findByRole(role);
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userDiplomaRepository.save(user);
    }

    public void deleteUser(long id){
        UserDiploma user = findById(id);
        if(user != null){
            user.setActive(false);
            userDiplomaRepository.save(user);
        }
    }

    public Page<UserDiploma> findAllUsers(Pageable pageable){
        return userDiplomaRepository.findAllByActiveIsTrue(pageable);
    }

    public UserDiploma findUserByEmail(String email) {
        return userDiplomaRepository.findByEmail(email);
    }

    public UserDiploma findById(long id){
        return userDiplomaRepository.getOne(id);
    }
}
