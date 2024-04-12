package com.face;

import com.face.server.IdAuthenticationServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.face.bean.result.FaceResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FaceEasyApplicationTests {

    @Autowired
    private IdAuthenticationServer idAuthenticationServer;


    @Test
    void contextLoads() {
    }

    @Test
    public void testAuthenticateId() {
        // Mock data for testing
        String faceName = "聂家辉";
        String idNo = "432503200204098770";

        // Call the method to be tested
        FaceResult faceResult = idAuthenticationServer.authenticateId(faceName, idNo);

        // Check if the result is not null
        assertNotNull(faceResult);

        // Check if code and message are properly set
        System.out.println("Code: " + faceResult.getCode());
        System.out.println("Message: " + faceResult.getMsg());

    }

}
