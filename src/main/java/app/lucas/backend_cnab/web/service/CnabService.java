package app.lucas.backend_cnab.web.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.Job;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


@Service
public class CnabService {

    private final Path fileStoragerLocation;
    private final JobLauncher jobLauncher;
    private final Job job;

    public CnabService(@Value("${file.upload-dir}")String fileUploadDir, @Qualifier("jobLauncherAsync") JobLauncher jobLauncher, Job job)  {
        this.fileStoragerLocation = Paths.get(fileUploadDir);
        this.jobLauncher = jobLauncher;
        this.job = job;
    }
    
    public void uploadCnabFile(MultipartFile file) throws Exception {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        var targetLocation = fileStoragerLocation.resolve(fileName);
        file.transferTo(targetLocation);

        var jobParameters = new JobParametersBuilder()
            .addJobParameter("cnab", file.getOriginalFilename(), String.class, true)
            .addJobParameter("cnabFile", "file:" + targetLocation.toString(), String.class)
            .toJobParameters();

        jobLauncher.run(job, jobParameters);
        
    }
}
