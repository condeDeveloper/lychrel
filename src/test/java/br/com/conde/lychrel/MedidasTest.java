package br.com.conde.lychrel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Os numeros do README, presos.
 *
 * <p>A varredura de um milhao leva cento e cinco segundos e nao cabe aqui: ela
 * e conferida dentro do proprio comando "fundo" do {@link Medidor}, que roda
 * num job separado e quebra se a medida mudar.
 */
class MedidasTest {

    @Test
    @DisplayName("ate dez mil: 9.751 resolvem e 249 nao chegam")
    void ateDezMil() {
        Varredura.Resumo r = Varredura.varrer(Medidor.LIMITE);
        assertThat(r.resolvidos()).isEqualTo(9_751);
        assertThat(r.candidatos()).isEqualTo(249);
        assertThat(r.resolvidos() + r.candidatos()).isEqualTo(10_000);
        assertThat(100.0 * r.candidatos() / 10_000)
                .isCloseTo(2.490, org.assertj.core.data.Offset.offset(0.001));
    }

    @Test
    @DisplayName("o recorde ate dez mil e o 89, com 24 passos")
    void oRecorde() {
        Varredura.Resumo r = Varredura.varrer(Medidor.LIMITE);
        assertThat(r.maiorNumeroDePassos()).isEqualTo(24);
        assertThat(r.ondeFoiORecorde()).isEqualTo(89);
        assertThat(r.digitosNoRecorde()).isEqualTo(13);
    }

    @Test
    @DisplayName("o salto de 79 para 89: de 6 passos para 24")
    void oSaltoDo79ParaO89() {
        List<long[]> recordes = Varredura.recordes(Medidor.LIMITE);
        assertThat(recordes).hasSize(6);
        assertThat(recordes.get(0)).containsExactly(1L, 1L, 1L);
        assertThat(recordes.get(1)).containsExactly(5L, 2L, 2L);
        assertThat(recordes.get(2)).containsExactly(59L, 3L, 4L);
        assertThat(recordes.get(3)).containsExactly(69L, 4L, 4L);
        assertThat(recordes.get(4)).containsExactly(79L, 6L, 5L);
        assertThat(recordes.get(5)).containsExactly(89L, 24L, 13L);
        // quatro vezes mais passos, entre dois numeros vizinhos
        assertThat(recordes.get(5)[1]).isEqualTo(4 * recordes.get(4)[1]);
    }

    @Test
    @DisplayName("o 196 e o menor candidato, e os vinte menores sao estes")
    void osMenoresCandidatos() {
        List<Integer> lista = Varredura.candidatos(Medidor.LIMITE);
        assertThat(lista).hasSize(249);
        assertThat(lista.get(0)).isEqualTo(196);
        assertThat(lista.subList(0, 13)).containsExactly(
                196, 295, 394, 493, 592, 689, 691, 788, 790, 879, 887, 978, 986);
    }

    @Test
    @DisplayName("os cinco primeiros candidatos tem todos soma de digitos 16")
    void osCandidatosVemEmFamilias() {
        for (int n : new int[] {196, 295, 394, 493, 592}) {
            int soma = 0;
            int resto = n;
            while (resto > 0) {
                soma += resto % 10;
                resto /= 10;
            }
            assertThat(soma).as("n = %d", n).isEqualTo(16);
        }
    }

    @Test
    @DisplayName("a distribuicao de passos oscila: 4 numeros em 14 passos, 83 em 15")
    void aDistribuicaoOscila() {
        int[] conta = Varredura.distribuicao(Medidor.LIMITE);
        assertThat(conta[1]).isEqualTo(2_838);
        assertThat(conta[2]).isEqualTo(2_892);
        assertThat(conta[2]).isGreaterThan(conta[1]);
        assertThat(conta[14]).isEqualTo(4);
        assertThat(conta[15]).isEqualTo(83);
        assertThat(conta[15]).isGreaterThan(conta[14] * 20);
        assertThat(conta[Varredura.TETO + 1]).isEqualTo(249);
    }

    @Test
    @DisplayName("os dois caminhos concordam em 6.302 passos")
    void juiz() {
        assertThat(Varredura.conferirOsDoisCaminhos(Medidor.LIMITE_JUIZ)).isEqualTo(6_302);
    }

    @Test
    @DisplayName("o long desiste no passo 40 para o 196, e no 31 para o 1997")
    void ondeOLongDesiste() {
        assertThat(Varredura.passoEmQueOLongDesiste(196)).isEqualTo(40);
        assertThat(Varredura.passoEmQueOLongDesiste(295)).isEqualTo(40);
        assertThat(Varredura.passoEmQueOLongDesiste(879)).isEqualTo(36);
        assertThat(Varredura.passoEmQueOLongDesiste(1997)).isEqualTo(31);
    }
}
