package com.lyra.caps.service;

import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;

import java.io.IOException;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;

@Service
public class DocumentExtractorService implements IDocumentExtractor {

    @Override
    public String extrair(MultipartFile file)  {
        try {
            Mat image = imdecode(new Mat(file.getBytes()), opencv_imgcodecs.IMREAD_GRAYSCALE);

            if (image.empty()) {
                throw new IOException("Erro ao carregar imagem");
            }

            Mat imageSuavizada = new Mat();

            opencv_imgproc.GaussianBlur(image, imageSuavizada, new Size(5, 5), 0);

            BufferedImage bufferedImage = matToBufferedImage(imageSuavizada);

            ITesseract tesseract = new Tesseract();

            // aqui você pode configurar o caminho para o arquivo de dados do Tesseract
            tesseract.setDatapath("home/lyra");

            String text = tesseract.doOCR(bufferedImage);
            
            return text;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TesseractException e) {            
            e.printStackTrace();
        }
        return null;
    }

    private static BufferedImage matToBufferedImage(Mat image) {
        try(OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
            Java2DFrameConverter converter2BufferedImage = new Java2DFrameConverter()) {
            return converter2BufferedImage.convert(converterToMat.convert(image));
        }
    }
}
