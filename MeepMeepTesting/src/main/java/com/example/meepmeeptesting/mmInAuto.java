package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class mmInAuto {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);
        Pose2d startPose = new Pose2d(-36, -62, Math.toRadians(90));
        Pose2d stackPose = new Pose2d(-58.5,-12.7,Math.toRadians(180));//orig:180
        Pose2d farmPose = new Pose2d(-23,-10,Math.toRadians(90)); //-9
        Pose2d approachPose = new Pose2d(-33.1,-10,Math.toRadians(180));//-37.1
        //
        Pose2d middlePark = new Pose2d(-35.8,-34.6,Math.toRadians(270));
        Pose2d leftPark =  new Pose2d(-60.8,-35.6,Math.toRadians(270));
        Pose2d rightPark =  new Pose2d(-10.8,-35.6,Math.toRadians(270));


        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)


                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(50, 50, Math.toRadians(80), Math.toRadians(75), 14)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPose)
                                .lineToLinearHeading(approachPose)
                                .lineToLinearHeading(farmPose)
                                .waitSeconds(1)
                                .UNSTABLE_addTemporalMarkerOffset(-4.5,()->{
                                    //bring up slides-interval
                                })
                                .UNSTABLE_addTemporalMarkerOffset(-2,()->{
                                    //bring up slides full
                                })
                                .waitSeconds(0.5)
                                .UNSTABLE_addTemporalMarkerOffset(1,()->{
                                    //bring slides down-partial
                                    //drop cone-release servo
                                })
                                .UNSTABLE_addTemporalMarkerOffset(-0.25,()->{
                                    //drop slides all the way
                                })
                                .lineToLinearHeading(approachPose)
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
                                .lineToLinearHeading(approachPose)
                                .lineToLinearHeading(farmPose) // (farmPose,Math.toRadians(42))
                                .waitSeconds(1)
                                .UNSTABLE_addTemporalMarkerOffset(-4.5,()->{
                                    //bring up slides-interval
                                })
                                .UNSTABLE_addTemporalMarkerOffset(-2,()->{
                                    //bring up slides full
                                })
                                .waitSeconds(0.5)
                                .UNSTABLE_addTemporalMarkerOffset(1,()->{
                                    //bring slides down-partial
                                    //drop cone-release servo
                                })
                                .UNSTABLE_addTemporalMarkerOffset(-0.25,()->{
                                    //drop slides all the way
                                })
                                .lineToLinearHeading(approachPose)



                                //parking stuffs
                                /*.lineToLinearHeading(middlePark)
                                .lineToLinearHeading(leftPark)
                                .lineToLinearHeading(rightPark)
                              */
                                .build()
                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}