import Semantic.AsmRunner;

public class Main {
    /**
     * 由于Java键盘缓冲区这方面的**设计，我无法做到多个输入参数，也无法让用户通过键盘输入路径
     *
     *
     */
    public static void main(String[] args) throws Exception {
        AsmRunner.main(args);
    }

    /**
     * 调这个方可以输入路径
     * @param path:含文件名的路径
     * @throws Exception
     */
    public Main(String path) throws Exception {
        new AsmRunner(path);
    }
}
