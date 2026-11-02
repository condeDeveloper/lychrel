package br.com.conde.lychrel;

import java.util.List;

/**
 * Roda as tabelas do README.
 *
 * <pre>
 * mvn -B -ntp exec:java -Dexec.args="varredura"
 * mvn -B -ntp exec:java -Dexec.args="recordes"
 * mvn -B -ntp exec:java -Dexec.args="candidatos"
 * mvn -B -ntp exec:java -Dexec.args="juiz"
 * mvn -B -ntp exec:java -Dexec.args="fundo"     (ate um milhao, job proprio)
 * </pre>
 */
public final class Medidor {

    public static final int LIMITE = 10_000;
    public static final int LIMITE_JUIZ = 2_000;
    public static final int LIMITE_FUNDO = 1_000_000;

    public static void main(String[] args) {
        String comando = args.length > 0 ? args[0] : "tudo";
        switch (comando) {
            case "varredura" -> varredura();
            case "recordes" -> recordes();
            case "candidatos" -> candidatos();
            case "juiz" -> juiz();
            case "fundo" -> fundo();
            case "tudo" -> {
                varredura();
                recordes();
                candidatos();
                juiz();
            }
            default -> {
                System.out.println("comando desconhecido: " + comando);
                System.exit(2);
            }
        }
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println(texto);
        System.out.println("-".repeat(texto.length()));
    }

    private static void varredura() {
        titulo("Varredura ate " + LIMITE + ", com teto de " + Varredura.TETO + " passos");
        Varredura.Resumo r = Varredura.varrer(LIMITE);
        System.out.printf("resolvidos  : %d (%.3f%%)%n",
                r.resolvidos(), 100.0 * r.resolvidos() / r.limite());
        System.out.printf("candidatos  : %d (%.3f%%)%n",
                r.candidatos(), 100.0 * r.candidatos() / r.limite());
        System.out.printf("recorde     : %d passos, em n = %d%n",
                r.maiorNumeroDePassos(), r.ondeFoiORecorde());
        System.out.printf("o palindromo do recorde tem %d digitos%n", r.digitosNoRecorde());

        System.out.println();
        System.out.printf("%-10s %s%n", "passos", "quantos n");
        int[] conta = Varredura.distribuicao(LIMITE);
        for (int i = 0; i <= Varredura.TETO; i++) {
            if (conta[i] > 0) {
                System.out.printf("%-10d %d%n", i, conta[i]);
            }
        }
        System.out.printf("%-10s %d%n", "nenhum", conta[Varredura.TETO + 1]);
    }

    private static void recordes() {
        titulo("Quem bate recorde de passos, ate " + LIMITE);
        System.out.printf("%-10s %-10s %s%n", "n", "passos", "digitos do palindromo");
        for (long[] linha : Varredura.recordes(LIMITE)) {
            System.out.printf("%-10d %-10d %d%n", linha[0], linha[1], linha[2]);
        }
    }

    private static void candidatos() {
        titulo("Os que nao chegam em " + Varredura.TETO + " passos, ate " + LIMITE);
        List<Integer> lista = Varredura.candidatos(LIMITE);
        System.out.printf("quantos: %d%n%n", lista.size());
        System.out.print("os trinta menores: ");
        for (int i = 0; i < Math.min(30, lista.size()); i++) {
            System.out.print(lista.get(i) + " ");
        }
        System.out.println();
        System.out.println();
        System.out.printf("o menor de todos e %d, que e o candidato mais famoso do assunto%n",
                lista.get(0));
        Reverso.Parada p = Reverso.parar(lista.get(0), Varredura.TETO);
        System.out.printf("depois de %d passos ele ja tem %d digitos, e nao e palindromo%n",
                Varredura.TETO, p.digitos());
    }

    private static void juiz() {
        titulo("BigInteger contra long, ate " + LIMITE_JUIZ);
        int passos = Varredura.conferirOsDoisCaminhos(LIMITE_JUIZ);
        System.out.printf("passos conferidos nos dois caminhos: %d, todos iguais%n", passos);
        System.out.println();
        System.out.println("Em que passo o long desiste de aguentar:");
        System.out.printf("%-10s %s%n", "n", "passo");
        for (int n : new int[] {196, 295, 394, 879, 1997}) {
            int onde = Varredura.passoEmQueOLongDesiste(n);
            System.out.printf("%-10d %s%n", n, onde < 0 ? "chegou a palindromo antes" : String.valueOf(onde));
        }
        System.out.println();
        System.out.println("E por isso que a varredura inteira roda em BigInteger.");
    }

    private static void fundo() {
        titulo("Varredura ate " + LIMITE_FUNDO);
        long comeco = System.nanoTime();
        Varredura.Resumo r = Varredura.varrer(LIMITE_FUNDO);
        System.out.printf("resolvidos : %d (%.4f%%)%n",
                r.resolvidos(), 100.0 * r.resolvidos() / r.limite());
        System.out.printf("candidatos : %d (%.4f%%)%n",
                r.candidatos(), 100.0 * r.candidatos() / r.limite());
        System.out.printf("recorde    : %d passos, em n = %d, palindromo de %d digitos%n",
                r.maiorNumeroDePassos(), r.ondeFoiORecorde(), r.digitosNoRecorde());
        System.out.printf("%ntempo: %.1f s%n", (System.nanoTime() - comeco) / 1e9);

        // Cento e cinco segundos nao cabem na bateria, que roda na matriz
        // inteira. A conferencia fica aqui: se a medida mudar, o job quebra.
        confere("resolvidos", r.resolvidos(), 884_035);
        confere("candidatos", r.candidatos(), 115_965);
        confere("recorde de passos", r.maiorNumeroDePassos(), 64);
        confere("onde foi o recorde", (int) r.ondeFoiORecorde(), 150_296);
        confere("digitos do palindromo", r.digitosNoRecorde(), 33);
        System.out.println();
        System.out.println("as cinco medidas do README bateram");
    }

    private static void confere(String nome, int medido, int esperado) {
        if (medido != esperado) {
            System.out.printf("MEDIDA MUDOU: %s era %d e veio %d%n", nome, esperado, medido);
            System.exit(1);
        }
    }
}
