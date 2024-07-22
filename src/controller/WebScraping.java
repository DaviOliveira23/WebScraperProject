package controller;

import model.Produto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WebScraping {

    public static void main(String[] args) {
        ArrayList<Produto> produtos = rasparDados();
        if (produtos != null) {
            criarPlanilha(produtos);
        } else {
            System.out.println("Falha na raspagem de dados. Planilha não será criada.");
        }
    }
    
    private static ArrayList<Produto> rasparDados() {
        // Carregando arquivo edge driver
        System.setProperty("webdriver.edge.driver", "resources/msedgedriver.exe");

        // Criando precauções para possíveis erros
        EdgeOptions options = new EdgeOptions();
        //options.addArguments("--headless"); //
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("window-size=1000,900");

        WebDriver driver = new EdgeDriver(options);
        // Requisitando código html da página
        driver.get("https://www.netshoes.com.br/");

        // Criando uma função para pesquisa através do input
        WebElement inputPesquisa = driver.findElement(By.xpath("//input[@id='search']"));


        Scanner scanner = new Scanner(System.in);
        System.out.print("\n\nEscreva o nome do produto: ");
        String produtoNome = scanner.nextLine();

        inputPesquisa.sendKeys(produtoNome);
        inputPesquisa.submit();

        // tempo para carregar a página
        waitForIt(1000);

        // Código para navegar a página até o fim e assim carregar os arquivos
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int scrollCount = 0;
        while (true) {
            long initialHeight = (long) js.executeScript("return document.body.scrollHeight");
            for (int i = 0; i < initialHeight; i += 500) {
                js.executeScript("window.scrollBy(0, 500);");
                waitForIt(500); // Wait for new elements to load
            }
            long newHeight = (long) js.executeScript("return document.body.scrollHeight");
            if (newHeight == initialHeight || scrollCount > 10) { // Break if no new elements are loaded or scroll limit reached
                break;
            }
            scrollCount++;
        }

        // Criando coleções do tipo List que recebem os respectivos elementos filtrados
        List<WebElement> nomeList = driver.findElements(By.className("card__description--name")); //procurando nomes e descriçãos dos produtos pela classe
        List<WebElement> precoList = driver.findElements(By.className("full-mounted")); // procurando preços dos produtos pela classe
        List<WebElement> parcelaList = driver.findElements(By.className("price__list--card")); // procurando preços dos produtos pela classe
        List<WebElement> imagemList = driver.findElements(By.xpath("//div/img[@data-src]")); // procurando pelas imagens dos produtos

        // Garantindo que todos os tamanhos das listas sejam iguais
        int size = Math.min(Math.min(Math.min(nomeList.size(), precoList.size()), parcelaList.size()), imagemList.size());

        ArrayList<Produto> produtos = new ArrayList<>();
        // Percorrendo as coleções e as adicionando a List Produto
        for (int i = 0; i < size; i++) {
            produtos.add(new Produto(
                    nomeList.get(i).getText(),
                    precoList.get(i).getText(),
                    parcelaList.get(i).getText(),
                    imagemList.get(i).getAttribute("src")
            ));
        }
        // Imprimindo quantidade de elementos encontrados para averiguar se não está havendo erros
        System.out.println("Nomes encontrados: " + nomeList.size());
        System.out.println("Preços encontrados: " + precoList.size());
        System.out.println("Parcelas encontradas: " + parcelaList.size());
        System.out.println("Imagens encontradas: " + imagemList.size());
        System.out.println("Produtos encontrados: " + produtos.size());
        System.out.println(produtos);


        driver.quit();
        // retornando a List produtos com seus elementos
        return produtos;
    }

    // Método para criar uma pausa entre os comandos
    private static void waitForIt(long tempo) {
        try {
            Thread.sleep(tempo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // Inicio do método para criação da planilha onde os dados irão ser alocados
    private static void criarPlanilha(ArrayList<Produto> produtos) {
        Workbook pastaTrabalho = new XSSFWorkbook();
        Sheet planilha = pastaTrabalho.createSheet("PRODUTOS");

        Font fonteNegrito = pastaTrabalho.createFont();
        fonteNegrito.setBold(true);

        CellStyle estiloNegrito = pastaTrabalho.createCellStyle();
        estiloNegrito.setFont(fonteNegrito);

        Row linha = planilha.createRow(0);
        Cell celula1 = linha.createCell(0);
        celula1.setCellValue("Nome/Descrição");
        celula1.setCellStyle(estiloNegrito);

        Cell celula2 = linha.createCell(1);
        celula2.setCellValue("Preços");
        celula2.setCellStyle(estiloNegrito);

        Cell celula3 = linha.createCell(2);
        celula3.setCellValue("Parcelas");
        celula3.setCellStyle(estiloNegrito);

        Cell celula4 = linha.createCell(3);
        celula4.setCellValue("Imagens");
        celula4.setCellStyle(estiloNegrito);

        planilha.autoSizeColumn(0);
        planilha.autoSizeColumn(1);
        planilha.autoSizeColumn(2);
        planilha.autoSizeColumn(3);

        if (produtos.size() > 0) {
            int i = 1;
            for (Produto produto : produtos) {
                Row linhaProduto = planilha.createRow(i);
                Cell celulaDescricao = linhaProduto.createCell(0);
                celulaDescricao.setCellValue(produto.getNomeP());

                Cell celulaPreco = linhaProduto.createCell(1);
                celulaPreco.setCellValue(produto.getValores());

                Cell celulaParcela = linhaProduto.createCell(2);
                celulaParcela.setCellValue(produto.getParcelaP());

                Cell celulaImagem = linhaProduto.createCell(3);
                celulaImagem.setCellValue(produto.getImageP());
                i++;
            }
        }

        try (FileOutputStream arquivo = new FileOutputStream("produtos.xlsx")) {
            pastaTrabalho.write(arquivo);
            JOptionPane.showMessageDialog(null, "Planilha criada com sucesso");
        } catch (Exception e) {
            System.out.println("Erro ao criar a planilha: " + e.getMessage());
        } finally {
            try {

                pastaTrabalho.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
}
