//package com.ppjt10.skifriend.securityTest;
//
//import com.ppjt10.skifriend.dto.SignupDto;
//import com.ppjt10.skifriend.entity.User;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.test.context.support.WithSecurityContextFactory;
//
//public class WithAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthUser> {
//
//    @Override
//    public SecurityContext createSecurityContext(WithAuthUser annotation) {
//        String username = annotation.username();
//
//
//        SignupDto.RequestDto requestDto = SignupDto.RequestDto.builder()
//                .username(username)
//                .nickname("버민")
//                .password("asdf12")
//                .phoneNum("01084746215")
//                .build();
//
//        User authUser = new User(requestDto, requestDto.getPassword());
//        UsernamePasswordAuthenticationToken token =
//                new UsernamePasswordAuthenticationToken(authUser, "password");
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(token);
//        return context;
//    }
//}
