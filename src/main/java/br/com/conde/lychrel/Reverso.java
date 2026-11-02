package br.com.conde.lychrel;

import java.math.BigInteger;

/**
 * Somar com o reverso, repetir ate virar palindromo.
 *
 * <pre>
 * 57  -> 57 + 75 = 132 -> 132 + 231 = 363   (palindromo em 2 passos)
 * 89  -> ... 24 passos ...                  -> 8813200023188
 * 196 -> ninguem sabe. Ja foram bilhoes de passos.
 * </pre>
 *
 * <p>Um numero que parece nunca chegar e chamado de candidato a Lychrel —
 * candidato, porque ninguem provou que algum deles de fato nunca chega. O
 * menor e o 196.
 *
 * <p>A conta cresce depressa: a partir de uns poucos passos, o valor estoura
 * qualquer inteiro de maquina. Aqui existem DUAS implementacoes: uma com
 * BigInteger, que nunca estoura, e uma com long, que avisa quando nao cabe
 * mais em vez de dar a volta em silencio.
 */
public final class Reverso {

    private Reverso() {
    }

    public static BigInteger reverter(BigInteger n) {
        return new BigInteger(new StringBuilder(n.toString()).reverse().toString());
    }

    public static boolean palindromo(BigInteger n) {
        String texto = n.toString();
        int i = 0;
        int j = texto.length() - 1;
        while (i < j) {
            if (texto.charAt(i++) != texto.charAt(j--)) {
                return false;
            }
        }
        return true;
    }

    /** Um passo: n mais o reverso de n. */
    public static BigInteger passo(BigInteger n) {
        return n.add(reverter(n));
    }

    /**
     * O caminho com long, que so vale enquanto couber.
     *
     * @return o proximo valor, ou -1 se a soma nao cabe mais em long
     */
    public static long passoEmLong(long n) {
        long revertido = 0;
        long resto = n;
        while (resto > 0) {
            long digito = resto % 10;
            if (revertido > (Long.MAX_VALUE - digito) / 10) {
                return -1;
            }
            revertido = revertido * 10 + digito;
            resto /= 10;
        }
        if (n > Long.MAX_VALUE - revertido) {
            return -1;
        }
        return n + revertido;
    }

    public static boolean palindromoEmLong(long n) {
        long revertido = 0;
        long resto = n;
        while (resto > 0) {
            revertido = revertido * 10 + resto % 10;
            resto /= 10;
        }
        return revertido == n;
    }

    /** O valor em que a trajetoria para, junto com os passos. */
    public record Parada(int passos, BigInteger valor, int digitos) {
    }

    public static Parada parar(long n, int teto) {
        BigInteger atual = BigInteger.valueOf(n);
        for (int passos = 1; passos <= teto; passos++) {
            atual = passo(atual);
            if (palindromo(atual)) {
                return new Parada(passos, atual, atual.toString().length());
            }
        }
        return new Parada(-1, atual, atual.toString().length());
    }
}
