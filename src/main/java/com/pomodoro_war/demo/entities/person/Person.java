package com.pomodoro_war.demo.entities.person;

import com.pomodoro_war.demo.entities.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "persons")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "person_type")
@Getter
@Setter
@NoArgsConstructor
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String name;
    protected int life;
    protected int armor;
    protected int lifeMax;
    protected int level;
    protected int totalArmor;

    // Estados
    protected boolean state;
    protected boolean stun;
    protected boolean inActivateTeam;
    protected int timeStun;

    // Estados de Veneno
    protected boolean poisoned;
    protected int poisonTurns;

    @ManyToOne
    @JoinColumn(name = "username")
    protected User user;

    // Mantenemos este constructor para inicializar los valores por defecto
    public Person(String name, int life, int armor) {
        this.name = name;
        this.life = life;
        this.armor = armor;
        this.lifeMax = life;
        this.state = true;
        this.stun = false;
        this.inActivateTeam = true;
        this.timeStun = 0;
        this.poisoned = false;
        this.poisonTurns = 0;
        this.level = 1;
        this.totalArmor = armor;
    }

    // Lógica de curación y daño (la mantenemos igual)
    public void receiveHeal(int healAmount) {
        if (this.state) {
            this.life += healAmount;
            if (this.life > this.lifeMax) {
                this.life = this.lifeMax;
            }
        }
    }

    public int receiveDamage(int damage) {
        int damageMax = (damage <= this.armor) ? 1 : (damage - this.armor);
        this.life -= damageMax;
        if (this.life <= 0) {
            this.life = 0;
            this.state = false;
        }
        return damageMax;
    }

    protected abstract int throwDices();
    public abstract int attack(Person enemy);
}