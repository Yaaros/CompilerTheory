package Lex.test;

import Lex.LexicalAnalyzer;
import org.junit.jupiter.api.Test;
public class LexTest {
    @Test
    public void test0_LexicalAnalyzer() throws Exception {
        System.out.println(LexicalAnalyzer.scan("""
                {
                    int a = 1;
                    int b = 2;
                    int c = a + b;
                }"""));
    }

    @Test
    public void test1_LexicalAnalyzer() throws Exception {
        System.out.println(LexicalAnalyzer.scan("""
                {
                    int p = 0;
                    if(p == 0){
                        int a = 1;
                        int b = 2;
                        int c = a + b;
                    }
                }"""

        ));
    }

    @Test
    public void test2_LexicalAnalyzer() throws Exception {
        System.out.println(LexicalAnalyzer.scan("""
                /* mmm */
                int a = 1/2;
                """
        ));
    }
}