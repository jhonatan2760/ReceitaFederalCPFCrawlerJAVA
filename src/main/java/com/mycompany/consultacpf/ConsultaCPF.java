package com.mycompany.consultacpf;

import com.jhonatansouza.PessoaFisica;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Jhonatan S. Souza
 */
public class ConsultaCPF {
    public static void main(String[] args) {
        try {
            Connection conn = Jsoup.connect("https://www.receita.fazenda.gov.br/Aplicacoes/SSL/ATCTA/CPF/ConsultaSituacao/ConsultaPublicaSonoro.asp")
                    .validateTLSCertificates(false)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
                    .method(Connection.Method.GET);
            
            Response r = conn.execute();
            
            Document doc = Jsoup.parse(r.body());
            
            Element el = doc.getElementById("imgCaptcha");
            String base = el.attr("src");
            String bas = base.subSequence(base.indexOf(",")+1, base.length()).toString();
            System.out.println(base.subSequence(base.indexOf(",")+1, base.length()));
            
            Image i = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(bas)));
            
            String cpf = JOptionPane.showInputDialog("Digite seu CPF");
            String data = JOptionPane.showInputDialog("Digite sua data de Nacimento dd/MM/yyyy");
            String captcha = JOptionPane.showInputDialog(new ImageIcon(i));
            
            Document d = Jsoup.connect("https://www.receita.fazenda.gov.br/Aplicacoes/SSL/ATCTA/CPF/ConsultaSituacao/ConsultaPublicaExibir.asp")
                    .data("idCheckedReCaptcha", "false")
                    .data("txtCPF", cpf)
                    .data("txtDataNascimento", data)
                    .data("txtTexto_captcha_serpro_gov_br", captcha)
                    .data("enviar", "Consultar")
                    .validateTLSCertificates(false)
                    .cookies(r.cookies())
                    .post();
            
            Document resposta = Jsoup.parse(d.body().toString());
            
            Elements els = resposta.getElementsByClass("clConteudoDados");
            System.out.println(els.toString());
            PessoaFisica pf = new PessoaFisica();

            pf.setNome(els.get(1).text());
            pf.setDataNascimento(els.get(2).text());
            pf.setSituacao(els.get(3).text());
            
            JFrame jf = new JFrame();
            jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jf.setLocationRelativeTo(null);
            jf.setLayout(new GridLayout());
            jf.setSize(320, 320);
            JPanel jp = new JPanel();
            
            jp.add(new JLabel(pf.getNome()));
            jp.add(new JLabel(pf.getDataNascimento()));
            jp.add(new JLabel(pf.getSituacao()));
            
            jf.getContentPane().add(jp);
            
            jf.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
