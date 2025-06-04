package org.zerock.b01.service;

import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.UserByDTO;

import java.util.List;

public interface UserByService {
    UserByDTO readOne(String uId);
    UserByDTO readOneForEmail(String uEmail);
    String registerUser(UserByDTO userByDTO);
    boolean checkEmailExists(String email);
    String changeUserProfile(String email);
    List<UserBy> readAllUser();
    void registerAdmin(UserBy userBy);
    void registerUnit(UserByDTO userByDTO);

    static class MidExistException extends Exception {
    }

    void removeUser(UserByDTO userByDTO);
}
