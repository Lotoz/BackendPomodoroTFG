package com.pomodoro_war.demo.data;

import com.pomodoro_war.demo.entities.enums.ZoneType;
import com.pomodoro_war.demo.entities.lore.Bestiary;
import com.pomodoro_war.demo.repositories.BestiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BestiaryDataLoader implements CommandLineRunner {

    private final BestiaryRepository bestiaryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (bestiaryRepository.count() == 0) {
            System.out.println("📜 Biblioteca vacía detectada. Inscribiendo los pergaminos de Héroes y Bestias...");

            Bestiary caballero = new Bestiary(
                    null, "Caballero", ZoneType.INITIAL,
                    "Guardián juramentado de la luz, vestido con acero pesado. Siempre está en la vanguardia protegiendo a los más débiles con su espada bastarda y su escudo inquebrantable. De los primeros en estar a tu lado para la aventura.",
                    "/bestiario/caballero.png", "hero"
            );

            Bestiary clerigo = new Bestiary(
                    null, "Clérigo", ZoneType.FOREST,
                    "Portador de la magia sagrada, capaz de curar a sus aliados y castigar a los herejes. Sin embargo, su poder es inestable: es el único héroe que, a la hora de atacar o curar, debe lanzar un dado y someterse a la voluntad del azar.",
                    "/bestiario/clerigo.png", "hero"
            );

            Bestiary mago = new Bestiary(
                    null, "Mago", ZoneType.LAVA,
                    "Un erudito de las artes arcanas. A diferencia de los demás héroes (que tiran 2 dados), el mago lanza 3 dados, pero la volatilidad de la magia le obliga a quedarse solo con el valor más alto y el más bajo. Además, domina hechizos ilusorios capaces de aturdir (stunear) a sus enemigos.",
                    "/bestiario/mago.png", "hero"
            );

            Bestiary enanoHeroe = new Bestiary(
                    null, "Enano", ZoneType.INITIAL,
                    "Robusto, ruidoso y con un hacha tan dura como su orgullo. Su experiencia subterránea lo hace excepcionalmente fuerte contra los duendes. Por desgracia, su baja estatura y estilo de combate lo hacen muy débil frente a la brutalidad de los orcos. De los primeros en estar a tu lado para la aventura.",
                    "/bestiario/enano.png", "hero"
            );

            Bestiary elfo = new Bestiary(
                    null, "Elfo", ZoneType.INITIAL,
                    "Ágil y letal, entrenado en la espesura del bosque. Su precisión lo hace temible y muy fuerte contra los orcos. Su sangre feérica lo hace completamente inmune al daño por veneno; sin embargo, mientras esté envenenado, la toxina corroe su equipo dejándolo sin armadura. De los primeros en estar a tu lado para la aventura.",
                    "/bestiario/elfo.png", "hero"
            );

            Bestiary naga = new Bestiary(
                    null, "Naga", ZoneType.FOREST,
                    "Una abominación ofidia de las ciénagas. Escupe y embadurna sus armas con una toxina capaz de envenenar a cualquier jugador, mermando su salud lentamente. El único que escapa a su enfermedad es el Elfo, aunque la naga logrará derretir su armadura.",
                    "/bestiario/naga.png", "beast"
            );

            Bestiary orco = new Bestiary(
                    null, "Orco", ZoneType.INITIAL,
                    "Una mole de músculos, furia y sed de sangre. No se detiene ante nada y sus golpes son tan devastadoramente fuertes que abollan el acero, reduciendo permanentemente la defensa de las armaduras de quienes reciben sus impactos. De los primeros enemigos que se interponen en tu camino.",
                    "/bestiario/orco.png", "beast"
            );

            Bestiary duende = new Bestiary(
                    null, "Duende", ZoneType.INITIAL,
                    "Pequeñas criaturas rastreras y ladronas. Atacan en grupo y aprovechan la oscuridad. Son el enemigo natural de los enanos, quienes conocen todas sus debilidades y los cazan sin piedad. De los primeros enemigos que se interponen en tu camino.",
                    "/bestiario/duende.png", "beast"
            );

            Bestiary brujo = new Bestiary(
                    null, "Brujo", ZoneType.LAVA,
                    "Un hechicero consumido por la magia negra. Aunque las bestias comunes lanzan solo 1 dado, el Brujo lanza 3, utilizando el dado más alto y el medio para asegurar impactos letales. Intenta usar magia paralizante para aturdir a sus víctimas, pero esto requiere tanta energía que a menudo fracasa.",
                    "/bestiario/brujo.png", "beast"
            );

            bestiaryRepository.saveAll(List.of(
                    caballero, clerigo, mago, enanoHeroe, elfo,
                    naga, orco, duende, brujo
            ));

            System.out.println("Bestiario inicializado con éxito. ¡Listo para la batalla!");
        }
    }
}