package br.com.conde.lychrel;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReversoTest {

    @Test
    @DisplayName("reverter e inverter a ordem dos digitos")
    void reverter() {
        assertThat(Reverso.reverter(BigInteger.valueOf(123))).isEqualTo(BigInteger.valueOf(321));
        assertThat(Reverso.reverter(BigInteger.valueOf(57))).isEqualTo(BigInteger.valueOf(75));
        assertThat(Reverso.reverter(BigInteger.valueOf(7))).isEqualTo(BigInteger.valueOf(7));
        // zeros no fim viram zeros na frente e somem
        assertThat(Reverso.reverter(BigInteger.valueOf(100))).isEqualTo(BigInteger.ONE);
    }

    @Test
    @DisplayName("palindromo reconhece os dois lados iguais")
    void palindromo() {
        assertThat(Reverso.palindromo(BigInteger.valueOf(7))).isTrue();
        assertThat(Reverso.palindromo(BigInteger.valueOf(11))).isTrue();
        assertThat(Reverso.palindromo(BigInteger.valueOf(363))).isTrue();
        assertThat(Reverso.palindromo(BigInteger.valueOf(1221))).isTrue();
        assertThat(Reverso.palindromo(BigInteger.valueOf(12))).isFalse();
        assertThat(Reverso.palindromo(BigInteger.valueOf(196))).isFalse();
    }

    @Test
    @DisplayName("o 57 vira palindromo em dois passos, como no livro")
    void oCaminhoDo57() {
        assertThat(Reverso.passo(BigInteger.valueOf(57))).isEqualTo(BigInteger.valueOf(132));
        assertThat(Reverso.passo(BigInteger.valueOf(132))).isEqualTo(BigInteger.valueOf(363));
        Reverso.Parada p = Reverso.parar(57, 50);
        assertThat(p.passos()).isEqualTo(2);
        assertThat(p.valor()).isEqualTo(BigInteger.valueOf(363));
    }

    @Test
    @DisplayName("o 89 precisa de 24 passos e termina em 8813200023188")
    void oCaminhoDo89() {
        Reverso.Parada p = Reverso.parar(89, 50);
        assertThat(p.passos()).isEqualTo(24);
        assertThat(p.valor()).isEqualTo(new BigInteger("8813200023188"));
        assertThat(p.digitos()).isEqualTo(13);
    }

    @Test
    @DisplayName("o 196 nao chega em duzentos passos, e ja tem 88 digitos")
    void o196NaoChega() {
        Reverso.Parada p = Reverso.parar(196, 200);
        assertThat(p.passos()).isEqualTo(-1);
        assertThat(p.digitos()).isEqualTo(88);
        assertThat(Reverso.palindromo(p.valor())).isFalse();
    }

    @Test
    @DisplayName("os dois caminhos concordam enquanto o long aguenta")
    void longConcordaComBigInteger() {
        for (long n = 1; n <= 500; n++) {
            long pequeno = n;
            BigInteger grande = BigInteger.valueOf(n);
            for (int passo = 0; passo < 30; passo++) {
                long proximo = Reverso.passoEmLong(pequeno);
                if (proximo < 0) {
                    break;
                }
                grande = Reverso.passo(grande);
                assertThat(grande.longValueExact()).as("n=%d passo=%d", n, passo).isEqualTo(proximo);
                assertThat(Reverso.palindromoEmLong(proximo))
                        .as("n=%d passo=%d", n, passo)
                        .isEqualTo(Reverso.palindromo(grande));
                pequeno = proximo;
                if (Reverso.palindromo(grande)) {
                    break;
                }
            }
        }
    }

    @Test
    @DisplayName("o long avisa quando nao cabe, em vez de dar a volta em silencio")
    void longAvisaEmVezDeEstourar() {
        assertThat(Reverso.passoEmLong(Long.MAX_VALUE)).isEqualTo(-1);
        // 8 x 10^18 mais o reverso dele passa de 1,6 x 10^19 e nao cabe
        assertThat(Reverso.passoEmLong(8_000_000_000_000_000_008L)).isEqualTo(-1);
        assertThat(Reverso.passoEmLong(57)).isEqualTo(132);

        // e o caso que me enganou: o reverso de 10^18 e 1, porque os zeros do
        // fim viram zeros da frente e somem. A soma cabe folgado.
        assertThat(Reverso.passoEmLong(1_000_000_000_000_000_000L))
                .isEqualTo(1_000_000_000_000_000_001L);
    }
}
