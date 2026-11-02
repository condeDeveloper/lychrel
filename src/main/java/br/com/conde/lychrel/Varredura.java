package br.com.conde.lychrel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/** Varre todo n ate um limite e conta em quantos passos ele vira palindromo. */
public final class Varredura {

    /** O teto de passos: acima disso o numero entra na lista de candidatos. */
    public static final int TETO = 200;

    public record Resumo(
            int limite,
            int resolvidos,
            int candidatos,
            int maiorNumeroDePassos,
            long ondeFoiORecorde,
            int digitosNoRecorde) {
    }

    private Varredura() {
    }

    public static Resumo varrer(int limite) {
        int resolvidos = 0;
        int candidatos = 0;
        int recorde = 0;
        long ondeRecorde = 0;
        int digitos = 0;
        for (int n = 1; n <= limite; n++) {
            Reverso.Parada p = Reverso.parar(n, TETO);
            if (p.passos() < 0) {
                candidatos++;
                continue;
            }
            resolvidos++;
            if (p.passos() > recorde) {
                recorde = p.passos();
                ondeRecorde = n;
                digitos = p.digitos();
            }
        }
        return new Resumo(limite, resolvidos, candidatos, recorde, ondeRecorde, digitos);
    }

    /** Os n que nao chegam a palindromo dentro do teto. */
    public static List<Integer> candidatos(int limite) {
        List<Integer> lista = new ArrayList<>();
        for (int n = 1; n <= limite; n++) {
            if (Reverso.parar(n, TETO).passos() < 0) {
                lista.add(n);
            }
        }
        return lista;
    }

    /** Quantos n precisam de cada numero de passos. */
    public static int[] distribuicao(int limite) {
        int[] conta = new int[TETO + 2];
        for (int n = 1; n <= limite; n++) {
            int passos = Reverso.parar(n, TETO).passos();
            conta[passos < 0 ? TETO + 1 : passos]++;
        }
        return conta;
    }

    /** Os n que batem recorde de passos, na ordem em que aparecem. */
    public static List<long[]> recordes(int limite) {
        List<long[]> lista = new ArrayList<>();
        int melhor = 0;
        for (int n = 1; n <= limite; n++) {
            Reverso.Parada p = Reverso.parar(n, TETO);
            if (p.passos() > melhor) {
                melhor = p.passos();
                lista.add(new long[] {n, p.passos(), p.digitos()});
            }
        }
        return lista;
    }

    /**
     * Confere o caminho com BigInteger contra o caminho com long, enquanto o
     * long ainda aguenta.
     *
     * @return quantos passos foram conferidos antes de o long desistir
     */
    public static int conferirOsDoisCaminhos(int limite) {
        int conferidos = 0;
        for (int n = 1; n <= limite; n++) {
            BigInteger grande = BigInteger.valueOf(n);
            long pequeno = n;
            for (int passo = 0; passo < TETO; passo++) {
                long proximoPequeno = Reverso.passoEmLong(pequeno);
                if (proximoPequeno < 0) {
                    break;
                }
                grande = Reverso.passo(grande);
                if (grande.longValueExact() != proximoPequeno) {
                    throw new IllegalStateException("os dois caminhos discordam em n = " + n
                            + ", passo " + passo + ": big " + grande + ", long " + proximoPequeno);
                }
                if (Reverso.palindromo(grande) != Reverso.palindromoEmLong(proximoPequeno)) {
                    throw new IllegalStateException(
                            "os dois testes de palindromo discordam em " + grande);
                }
                conferidos++;
                pequeno = proximoPequeno;
                if (Reverso.palindromo(grande)) {
                    break;
                }
            }
        }
        return conferidos;
    }

    /** Em que passo o long desiste, por numero de partida. */
    public static int passoEmQueOLongDesiste(long n) {
        long atual = n;
        for (int passo = 1; passo <= TETO; passo++) {
            long proximo = Reverso.passoEmLong(atual);
            if (proximo < 0) {
                return passo;
            }
            atual = proximo;
            if (Reverso.palindromoEmLong(atual)) {
                return -1;
            }
        }
        return -1;
    }
}
