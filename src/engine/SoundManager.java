package engine;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class SoundManager {
    private static SoundManager instance;
    static Map<String, String[]> EffectSounds;
    static Map<String, Clip> BGMs;
    static String[][] ESFiles;
    static String[][] BGMFiles;
    private static Logger logger;
/**
* Code Description
* Base: BGM files are stored in res/sound/BGM
*       ES files are stored in res/sound/ES, and should be specified in res/ES file in the format: [Type];[Alias];[File Name];[Volume]
        *         -> Type: bgm, es
*         -> Volume: A value between -80.0 and 6.0
        * Usage
* Manager Call: Use getInstance() to call the manager
* BGM Call: Use playBGM(String fileName) to play BGM. It will loop indefinitely, and you can stop it by calling stopBGM()
* ES Call: Use playES(String effectName) to play sound effects. The same ES can be called simultaneously.
* Change BGM Volume: Use modifyBGMVolume(String name, float volume) to modify the volume
* Change ES Volume: Use modifyESVolume(String name, float volume) to modify the volume
*
* All the above functionalities have been implemented and tested successfully.
*/


    private SoundManager() {
        try {
            logger = Core.getLogger();
            // 리소스 파일 "res/sound"를 읽기 위한 InputStream 생성
            InputStream inputStream = SoundManager.class.getClassLoader().getResourceAsStream("sound");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            int ESFileCount = 0;
            int BGMFileCount = 0;
            String line;
            while ((line = br.readLine()) != null) {
                // 각 줄을 세미콜론으로 분리
                String[] parts = line.split(";");
                if (parts.length > 0) {
                    if ("bgm".equalsIgnoreCase(parts[0])) {
                        BGMFileCount++;
                    } else if ("es".equalsIgnoreCase(parts[0])) {
                        ESFileCount++;
                    }
                }
            }
            EffectSounds = new HashMap<String, String[]>(ESFileCount);
            BGMs = new HashMap<String, Clip>(BGMFileCount);
            ESFiles = new String[ESFileCount][3];
            BGMFiles = new String[BGMFileCount][3];

            int idx = 0;
            int idy = 0;

            inputStream = SoundManager.class.getClassLoader().getResourceAsStream("sound");

            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                // 세미콜론(;)로 구분된 데이터를 파싱
                String[] data = line.split(";");
                if(data[0].equals("es")){
                    ESFiles[idx][0] = data[1];
                    ESFiles[idx][1] = data[2];
                    ESFiles[idx][2] = data[3];
                    this.presetEffectSound(ESFiles[idx][0], "res/Sound.assets/ES/"+ESFiles[idx][1], Float.parseFloat(ESFiles[idx][2]));
                    idx += 1;
                }else if(data[0].equals("bgm")){
                    BGMFiles[idy][0] = data[1];
                    BGMFiles[idy][1] = data[2];
                    BGMFiles[idy][2] = data[3];
                    this.preloadBGM(BGMFiles[idy][0], "res/Sound.assets/BGM/"+BGMFiles[idy][1], Float.parseFloat(BGMFiles[idy][2]));
                    idy += 1;
                }
            }
        } catch (IOException e) {
            logger.info(String.valueOf(e));
        }
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void stopAllBGM() {
        for (Clip c : BGMs.values()) {
            if (c != null)
                c.stop();
        }
    }

    public void preloadBGM(String name, String filePath, float volume) {
        try {
            if (!BGMs.containsKey(name)) {
                URL resourceURL = SoundManager.class.getClassLoader().getResource("Sound.assets/BGM/background.wav");
                if (resourceURL == null) {
                    logger.warning("Resource not found: Sound.assets/BGM/background.wav");
                } else {
                    logger.info("Resource found: " + resourceURL);
                }


                InputStream inputStream = SoundManager.class.getClassLoader().getResourceAsStream(filePath.substring(4));
                if (inputStream == null) {
                    logger.warning("File not found: " + filePath);
                    return;
                }

                BufferedInputStream bufferedStream = new BufferedInputStream(inputStream); // 버퍼링으로 처리 속도 향상
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedStream);

                AudioFormat baseFormat = audioStream.getFormat();
                AudioFormat targetFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        44100,
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        44100,
                        false
                );

                AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, audioStream);

                Clip clip = AudioSystem.getClip();
                clip.open(convertedStream);

                // 볼륨 조절
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volume);

                // 해시맵에 추가
                BGMs.put(name, clip); // 미리 로드하여 맵에 저장
                logger.info(name + " load complete");
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.warning("Error loading BGM: " + e.getMessage());
        }
    }



    public int playPreloadedBGM(String name){
        Clip clip = BGMs.get(name);
        if(clip != null){
            clip.setFramePosition(0);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            return 1;
        }else{
            return 0;
        }
    }

//    public void presetEffectSound(String name, String filePath, float volume) {
//        try {
//            if (!EffectSounds.containsKey(name)) {
//                InputStream inputStream = SoundManager.class.getClassLoader().getResourceAsStream(filePath.substring(4));
////                System.out.println("Sound.assets/ES/" + name+".wav");
//                System.out.println(inputStream);
////                System.out.println(filePath.substring(4));
//
//                String[] tmp = {filePath, String.valueOf(volume)};
//                EffectSounds.put(name, tmp);
//                logger.info(name+ "is set");
//            }
//        } catch (Exception e) {
//            logger.info(String.valueOf(e));
//        }
//    }

    public void presetEffectSound(String name, String filePath, float volume) {
        try {
            if (!EffectSounds.containsKey(name)) {
                InputStream inputStream = SoundManager.class.getClassLoader().getResourceAsStream(filePath.substring(4));
//                System.out.println("Sound.assets/ES/" + name+".wav");
//                System.out.println(inputStream);
//                System.out.println(SoundManager.class.getResourceAsStream(""));

                if (inputStream == null) {
                    logger.warning("Sound file not found: " + filePath);
                    return;
                }
                String[] tmp = {filePath, String.valueOf(volume)};
                EffectSounds.put(name, tmp);
                logger.info(name + " is set");
            }
        } catch (Exception e) {
            logger.warning("Error setting effect sound: " + e.getMessage());
        }
    }

    public int playEffectSound(String name) {
        try {
            if (EffectSounds.containsKey(name)) {
                String[] tmp = EffectSounds.get(name);

                // 리소스 파일을 InputStream으로 읽기
                InputStream inputStream = SoundManager.class.getClassLoader().getResourceAsStream(tmp[0].substring(4));
                if (inputStream == null) {
                    logger.warning("Sound file not found: " + tmp[0]);
                    return 0;
                }

                // InputStream을 AudioInputStream으로 변환
                BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedStream);

                // 오디오 포맷 변환
                AudioFormat baseFormat = audioStream.getFormat();
                AudioFormat targetFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        44100,
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        44100,
                        false
                );

                AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, audioStream);

                // Clip 초기화
                Clip clip = AudioSystem.getClip();
                clip.open(convertedStream);

                // 볼륨 조절
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(Float.parseFloat(tmp[1]));

                // 재생 및 종료 이벤트 리스너
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

                logger.info(tmp[0] + " load complete");
                return 1;
            }

            logger.info("There is no ES: " + name);
            return 0;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.warning("Error playing effect sound: " + e.getMessage());
            return 0;
        }
    }

    public int playBGM(String name){
        try {
            stopAllBGM();
            new Thread(() -> playPreloadedBGM(name)).start();
            return 1;
        }catch (Exception e){
            logger.info(String.valueOf(e));
            return 0;
        }
    }

    public int playES(String name){
        try {
            new Thread(() -> playEffectSound(name)).start();
            return 1;
        }catch (Exception e){
            logger.info(String.valueOf(e));
            return 0;
        }
    }

    public int modifyBGMVolume(String name, float volume){
        if(volume > 6 || volume < -80){
            logger.info("Error : volume is out of index!!!!!");
            logger.info("input volume : "+ volume);
            return 0;
        }
        if(BGMs.containsKey(name)){
            Clip clip = BGMs.get(name);
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(volume);
            return 1;
        }
        return 0;
    }

    public int modifyESVolume(String name, float volume){
        if(volume > 6 || volume < -80){
            logger.info("Error : volume is out of index!!!!!");
            logger.info("input volume : "+ volume);
            return 0;
        }
        if(EffectSounds.containsKey(name)){
            EffectSounds.get(name)[1] = String.valueOf(volume);
            return 1;
        }else{
            return 0;
        }
    }

    public int modifyAllESVolume(int level) {
        float volume = -79;
        for (Map.Entry<String, String[]> entry : EffectSounds.entrySet()) {
            String esName = entry.getKey();
            float currentVolume = getESFileVolume(esName);

            // 볼륨 조절
            if(level == 1){
                volume = (currentVolume - 80) / 3;
            } else if(level == 2){
                volume = (2 * currentVolume - 40) / 3;
            } else if(level == 3){
                volume = currentVolume;
            } else if(level == 4){
                volume = (6 + currentVolume) / 2;
            } else if(level == 5){
                volume = 6;
            } else if(level == 0){
                volume = -80;
            }
            modifyESVolume(esName, volume);
        }
        logger.info("Change level : " + level);
        return 1;
    }

    public int modifyAllBGMVolume(int level) {
        float volume =79;
        for (Map.Entry<String, Clip> entry : BGMs.entrySet()) {
            String bgmName = entry.getKey();
            float currentVolume = getBGMFileVolume(bgmName);

            // 볼륨 조절
            if(level == 1){
                volume = (currentVolume - 80) / 3;
            } else if(level == 2){
                volume = (2 * currentVolume - 40) / 3;
            } else if(level == 3){
                volume = currentVolume;
            } else if(level == 4){
                volume = (6 + currentVolume) / 2;
            } else if(level == 5){
                volume = 6;
            } else if(level == 0){
                volume = -80;
            }
            modifyBGMVolume(bgmName, volume);
        }
        logger.info("Change level : " +level);
        return 1;
    }


    public float getESFileVolume(String esName) {
        // BGMFiles 배열에서 bgmName에 해당하는 초기 볼륨 값을 찾아서 반환
        for (String[] esFile : ESFiles) {
            if (esFile[0].equals(esName)) {
                return Float.parseFloat(esFile[2]); // BGMFiles 배열의 3번째 값이 volume
            }
        }
        return -80.0f;
    }
    public float getBGMFileVolume(String bgmName) {
        // BGMFiles 배열에서 bgmName에 해당하는 초기 볼륨 값을 찾아서 반환
        for (String[] bgmFile : BGMFiles) {
            if (bgmFile[0].equals(bgmName)) {
                return Float.parseFloat(bgmFile[2]); // BGMFiles 배열의 3번째 값이 volume
            }
        }
        return -80.0f;
    }




    // ksm
    public void playShipDieSounds() {
        playES("ally_airship_destroy_explosion");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.info(String.valueOf(e));
            }
            playES("ally_airship_destroy_die");
        }).start();
    }
}