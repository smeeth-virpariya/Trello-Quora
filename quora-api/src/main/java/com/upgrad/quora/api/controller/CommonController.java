package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonUserService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

  @Autowired CommonUserService commonUserService;

  /**
   * Get the user details provided the userId.
   *
   * @param accessToken Access token to authenticate the user who is requesting for user details.
   * @param userId user id of the user whose details has to be fetched.
   * @return
   * @throws AuthorizationFailedException - if the access token is invalid or already logged out.
   * @throws UserNotFoundException - if the user with given id is not present in the records.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      path = "/userprofile/{userId}",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UserDetailsResponse> getProfile(
      @RequestHeader("authorization") final String accessToken,
      @PathVariable("userId") final String userId)
      throws AuthorizationFailedException, UserNotFoundException {
    commonUserService.checkIfTokenIsValid(accessToken);
    UserEntity userEntity = commonUserService.getUserById(userId);
    UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
    userDetailsResponse.setFirstName(userEntity.getFirstName());
    userDetailsResponse.setLastName(userEntity.getLastName());
    userDetailsResponse.setUserName(userEntity.getUserName());
    userDetailsResponse.setEmailAddress(userEntity.getEmail());
    userDetailsResponse.setDob(userEntity.getDob());
    userDetailsResponse.setAboutMe(userEntity.getAboutMe());
    userDetailsResponse.setContactNumber(userEntity.getContactNumber());
    userDetailsResponse.setCountry(userEntity.getCountry());
    return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
  }
}
