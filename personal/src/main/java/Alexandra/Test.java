package Alexandra;

import model.Post;

import java.time.LocalDateTime;

public class Test {
    public static void main(String[] args) {

        Post ps = new Post(2,"sdfghjkhgf", LocalDateTime.now(),false,null);
        System.out.println(ps);

    }


}
