package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.SampleMecanumDrive;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);
        Pose2d startPose = new Pose2d(-35, -65, Math.toRadians(90));
        Pose2d stackPose = new Pose2d(-58.5,-12.7,Math.toRadians(180));
        Pose2d farmPose = new Pose2d(-33.9,-8.5,Math.toRadians(42));
        Pose2d brotherPose = new Pose2d(-38.1,-12.7,Math.toRadians(90));

        Pose2d beginnerPose = new Pose2d(-35.1,-13,Math.toRadians(90));

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)


                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(43, 30, Math.toRadians(80), Math.toRadians(75), 14)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPose)
                                .lineToLinearHeading(beginnerPose)
                                .lineToLinearHeading(farmPose)
                                .waitSeconds(1)
                                .UNSTABLE_addTemporalMarkerOffset(-4.5,()->{
                                    //bring up slides-interval
                                })
                                .UNSTABLE_addTemporalMarkerOffset(-2,()->{
                                    //bring up slides full
                                })
                                .forward(4)
                                .waitSeconds(0.5)
                                .UNSTABLE_addTemporalMarkerOffset(1,()->{
                                    //bring slides down-partial
                                    //drop cone-release servo
                                })
                                .UNSTABLE_addTemporalMarkerOffset(-0.25,()->{
                                    //drop slides all the way
                                })
                                .lineToLinearHeading(brotherPose)
                                .lineToLinearHeading(stackPose)//add speed constraints//going to pick stacks
                                .waitSeconds(0.5)
                                .UNSTABLE_addTemporalMarkerOffset(-3,()-> {
                                    //open servo
                                    //shift slides
                                })
                                .UNSTABLE_addTemporalMarkerOffset(-1,()-> {
                                    //close servo
                                    //bring up slides(small preset)
                                })
                                .lineToLinearHeading(brotherPose)
                                .lineToLinearHeading(farmPose)
                                .waitSeconds(1)
                                .UNSTABLE_addTemporalMarkerOffset(-4.5,()->{
                                    //bring up slides-interval
                                })
                                .UNSTABLE_addTemporalMarkerOffset(-2,()->{
                                    //bring up slides full
                                })
                                .forward(4)
                                .waitSeconds(0.5)
                                .UNSTABLE_addTemporalMarkerOffset(1,()->{
                                    //bring slides down-partial
                                    //drop cone-release servo
                                })
                                .UNSTABLE_addTemporalMarkerOffset(-0.25,()->{
                                    //drop slides all the way
                                })
                                .lineToLinearHeading(brotherPose)
                                .lineToLinearHeading(stackPose)
                                .build()
                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}