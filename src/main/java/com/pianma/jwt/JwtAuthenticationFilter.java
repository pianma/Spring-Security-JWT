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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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
        //principalDetailsService의 loadByUsername()함수가 실행된 후 정상이면 authenticatio이 리턴됨.
        //DB에 잇는 useranme과 password가 일치함.
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        //authentication 객체가 세션영역에 저장됨.
        //세션 영역에 있는 authentication에 있는 principal이라는 객체를 들고와서
        //username출력이 되면 => 로그인이 되었다는 뜻
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("로그인 완료됨 :" + principalDetails.getUser().getUsername()); //로그인이 정상적으로 되었다는 뜻.
        //authentication 객체가 session영역에 저장을 해야하고 그 방법이 return 해주면 됨.
        //리턴의 이유는 권환 관리를 security가 대신 해주기 때문에 편하려고 하는거임.
        //굳이 jwt토큰을 쓰면서 세션을 만들 이유가 없음. 단지 권한 처리때문에 session 넣어준다.
        return authentication;
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    //attemptAuthentication실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행됨.
    //jwt토큰을 만들어서 request요청한 사용자에게 jwt토큰을 response해주면됨.
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
    System.out.println("successfulAuthentication 실행됨 : 인증완료가 되었다는 뜻");
    super.successfulAuthentication(request, response, chain, authResult);
  }
}
