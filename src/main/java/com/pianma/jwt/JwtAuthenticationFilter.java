package com.pianma.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pianma.jwt.auth.PrincipalDetails;
import com.pianma.jwt.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

//스프링 시큐리티에서 UsernamePasswordAuthenticationFilter가 있음.
// /login 요청해서 username, password 전송하면 (post)
// UsernamePasswordAuthenticationFilter 동작을 함
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  // /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 1.username, password 받아서
        // 2. 정상인지 로그인 시도를 해봄. authenticationManager로 로그인 시도를 하면
        // PrincipalDetailsService가 호출 loadUserByUsername() 함수 실행됨.
        // 3.PrincipalDetails를 세션에 담고
        // 4.JWT토큰을 만들어서 응답해주면됨


      System.out.println("JwtAuthenticationFilter 시도중");

      try {
//        BufferedReader br = request.getReader();
//
//        String input = null;
//        while((input = br.readLine()) != null){
//          System.out.println(input);
//        }
        ObjectMapper om = new ObjectMapper(); //json 클래스 파싱해줌
        User user = om.readValue(request.getInputStream(), User.class);
        System.out.println(user);

        //토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        //로그인 시도
        //principalDetailsService의 loadByUsername()함수가 실행됨
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        //authentication 객체가 세션영역에 저장됨.
        //세션 영역에 있는 authentication에 있는 principal이라는 객체를 들고와서
        //username출력이 되면 => 로그인이 되었다는 뜻
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println(principalDetails.getUser().getUsername());

        return authentication;
      } catch (IOException e) {
        e.printStackTrace();
      }

      System.out.println("====================");
      return null;
    }
}
