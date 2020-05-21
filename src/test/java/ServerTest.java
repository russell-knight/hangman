import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class ServerTest {

    @Test
    public void testConvertCharListToString() {
        List<Character> l = new ArrayList<>();
        l.add('P'); l.add('e'); l.add('t'); l.add('e'); l.add('r');

        assertEquals("Peter", Server.convertArrToString(l));
    }
}


/*
    // Converts a List<Character> to a String and returns the String
protected static String convertArrToString(List<Character> list) {
    StringBuilder sb = new StringBuilder(list.size());
    for (Character c: list)
        sb.append(c);
    return sb.toString();
}
 */