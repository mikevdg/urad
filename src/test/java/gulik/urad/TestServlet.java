package gulik.urad;

import static org.junit.Assert.*;
import org.mockito.Mockito;
import java.io.*;
import javax.servlet.http.*;
import gulik.dolichos.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import org.junit.Test;

public class TestServlet extends Mockito {
    private class TestOutputStream extends ServletOutputStream {
        private StringWriter writer;
        
        public TestOutputStream(StringWriter s) {
            this.writer = s;
        }
        
        public void write(int i) {
            writer.write((char)i);
        }
        
        public void setWriteListener(WriteListener w) {
        }
        
        public boolean isReady() {
            return true;
        }
    }
    
    @Test
    public void testServlet() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);    

        when(request.getMethod()).thenReturn("GET");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(response.getOutputStream()).thenReturn(new TestOutputStream(stringWriter));

        new ODataServlet().service(request, response);

        //verify(request, atLeast(1)).getParameter("asdf");
        writer.flush(); 
        
        System.out.println(stringWriter.toString());
        
        assertTrue(stringWriter.toString().contains("Vegetable"));
        // working here. 
    }
}