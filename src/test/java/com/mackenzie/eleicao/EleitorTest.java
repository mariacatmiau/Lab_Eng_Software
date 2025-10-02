/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mackenzie.eleicao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Gustavo
 */
public class EleitorTest {
    
    public EleitorTest() {
    }
    
    Eleitor objEleitor = new Eleitor();

    @Test
    public void testMtClasssificarEleitorInvalido() {
        System.out.println("Teste da classe Idade Invalida");
        assertEquals("Idade Invalida", objEleitor.mtClasssificarEleitor(-1));
        assertEquals("Idade Invalida", objEleitor.mtClasssificarEleitor(131));
    }
    
    @Test
    public void testMtClasssificarEleitorNaoPertido() {
        System.out.println("Teste da classe Nao Permitido");
        assertEquals("Nao permitido", objEleitor.mtClasssificarEleitor(0));
        assertEquals("Nao permitido", objEleitor.mtClasssificarEleitor(1));
        assertEquals("Nao permitido", objEleitor.mtClasssificarEleitor(14));
        assertEquals("Nao permitido", objEleitor.mtClasssificarEleitor(15));
    }
    
    @Test
    public void testMtClasssificarEleitorFacultativo() {
        System.out.println("Teste da classe Facultativo");
        assertEquals("Facultativo", objEleitor.mtClasssificarEleitor(16));
        assertEquals("Facultativo", objEleitor.mtClasssificarEleitor(17));
        assertEquals("Facultativo", objEleitor.mtClasssificarEleitor(70));
        assertEquals("Facultativo", objEleitor.mtClasssificarEleitor(71));
        assertEquals("Facultativo", objEleitor.mtClasssificarEleitor(129));
        assertEquals("Facultativo", objEleitor.mtClasssificarEleitor(130));
    }
    
    @Test
    public void testMtClasssificarEleitorObrigatorio() {
        System.out.println("Teste da classe Obrigatorio");
        assertEquals("Obrigatorio", objEleitor.mtClasssificarEleitor(18));
        assertEquals("Obrigatorio", objEleitor.mtClasssificarEleitor(19));
        assertEquals("Obrigatorio", objEleitor.mtClasssificarEleitor(68));
        assertEquals("Obrigatorio", objEleitor.mtClasssificarEleitor(69));
    }
    
}
