/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor..
 */
//testando com a turma 6N
//testando com a turma 6P antes de sextar
package com.mackenzie.eleicao;

/**
 *
 * @author Gustavo
 */
public class Eleitor {
    
    public String mtClasssificarEleitor(int mtInIdade){
        
        String mtOutClassificacao;
              
        if (mtInIdade >= 18 && mtInIdade < 70){
            mtOutClassificacao = "Obrigatorio";
        }
        else if (mtInIdade >= 0 && mtInIdade <= 15){
            mtOutClassificacao = "Nao permitido";
        }
        else if ((mtInIdade > 15 && mtInIdade <= 17) 
                || (mtInIdade >= 70 && mtInIdade <= 130)){
            mtOutClassificacao = "Facultativo";
        }
        else{
            mtOutClassificacao = "Idade Invalida";
        }

        return mtOutClassificacao;
        
    }
    
}



