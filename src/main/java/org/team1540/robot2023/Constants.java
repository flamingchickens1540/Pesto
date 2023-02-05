package org.team1540.robot2023;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;
import org.team1540.lib.util.COTSFalconSwerveConstants;
import org.team1540.lib.util.SwerveModuleConstants;
import org.team1540.robot2023.utils.swerve.ModuleCorner;


public final class Constants {
    private static final boolean isNewRobot = false;

    public static final class Swerve {
        public static final String canbus = isNewRobot ? "swerve" : ""; // Set to "" to use RIO's can bus

        public static final COTSFalconSwerveConstants chosenModule = COTSFalconSwerveConstants.SDSMK4i(COTSFalconSwerveConstants.driveGearRatios.SDSMK4i_L2);

        /* Drivetrain Constants */
        public static final double trackWidth = Units.inchesToMeters(26);
        public static final double wheelBase = Units.inchesToMeters(23);
        public static final double wheelCircumference = chosenModule.wheelCircumference;

        /* Swerve Kinematics
         * No need to ever change this unless you are not doing a traditional rectangular/square 4 module swerve */
        public static final SwerveDriveKinematics swerveKinematics = new SwerveDriveKinematics(
                new Translation2d(wheelBase / 2.0, trackWidth / 2.0),
                new Translation2d(wheelBase / 2.0, -trackWidth / 2.0),
                new Translation2d(-wheelBase / 2.0, trackWidth / 2.0),
                new Translation2d(-wheelBase / 2.0, -trackWidth / 2.0));

        /* Module Gear Ratios */
        public static final double driveGearRatio = chosenModule.driveGearRatio;
        public static final double angleGearRatio = chosenModule.angleGearRatio;

        /* Motor Inverts */
        public static final boolean angleMotorInvert = chosenModule.angleMotorInvert;
        public static final boolean driveMotorInvert = true;

        /* Angle Encoder Invert */
        public static final boolean canCoderInvert = chosenModule.canCoderInvert;

        /* Swerve Current Limiting */
        public static final int angleContinuousCurrentLimit = 25;
        public static final int anglePeakCurrentLimit = 40;
        public static final double anglePeakCurrentDuration = 0.1;
        public static final boolean angleEnableCurrentLimit = true;

        public static final int driveContinuousCurrentLimit = 35;
        public static final int drivePeakCurrentLimit = 60;
        public static final double drivePeakCurrentDuration = 0.1;
        public static final boolean driveEnableCurrentLimit = true;

        /* These values are used by the drive falcon to ramp in open loop and closed loop driving.
         * We found a small open loop ramp (0.25) helps with tread wear, tipping, etc */
        public static final double openLoopRamp = 0.25;
        public static final double closedLoopRamp = 0.0;

        /* Angle Motor PID Values */
        public static final double angleKP = chosenModule.angleKP;
        public static final double angleKI = chosenModule.angleKI;
        public static final double angleKD = chosenModule.angleKD;
        public static final double angleKF = chosenModule.angleKF;

        /* Drive Motor PID Values */
//        public static final double driveKP = 0.34982; //TODO: This must be tuned to specific robot
        public static final double driveKP = 0.05;
        public static final double driveKI = 0.0;
        public static final double driveKD = 0.0;
        public static final double driveKF = 0.0;

        /* Drive Motor Characterization Values
         * Divide SYSID values by 12 to convert from volts to percent output for CTRE */
        public static final double driveKS = (0.18132 / 12); //TODO: This must be tuned to specific robot
        public static final double driveKV = (2.2502 / 12);
        public static final double driveKA = (0.2682 / 12);

        /* Swerve Profiling Values */
        /**
         * Meters per Second
         */
        public static final double maxVelocity = 6380.0 / 60.0 *
                (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0) *
                0.10033 * Math.PI;
        public static final double maxAngularSpeed = maxVelocity /
                Math.hypot(trackWidth / 2.0, wheelBase / 2.0);

        /* Neutral Modes */
        public static final NeutralMode angleNeutralMode = NeutralMode.Coast;
        public static final NeutralMode driveNeutralMode = NeutralMode.Brake;


        /* Module Specific Constants */
        /* Front Left Module - Module 0 */
        public static final class Mod0 {
            private static final int moduleID = isNewRobot ? 8: 4;
            public static final SwerveModuleConstants constants = new SwerveModuleConstants(moduleID, ModuleCorner.FRONT_LEFT);
        }

        /* Front Right Module - Module 1 */
        public static final class Mod1 {
            private static final int moduleID = isNewRobot ? 7 : 1;

            public static final SwerveModuleConstants constants = new SwerveModuleConstants(moduleID, ModuleCorner.FRONT_RIGHT);
        }

        /* Back Left Module - Module 2 */
        public static final class Mod2 {
            private static final int moduleID = isNewRobot ? 5:3;
            public static final SwerveModuleConstants constants = new SwerveModuleConstants(moduleID, ModuleCorner.REAR_LEFT);
        }

        /* Back Right Module - Module 3 */
        public static final class Mod3 {
            private static final int moduleID = isNewRobot? 6 : 2;
            public static final SwerveModuleConstants constants = new SwerveModuleConstants(moduleID, ModuleCorner.REAR_RIGHT);
        }
    }

    public static final class ArmConstants {
        public static final int PIVOT1_ID = 10;
        public static final int PIVOT2_ID = 11;
        public static final int TELESCOPE_ID = 12;
        public static final int PIGEON_ID = 13;

        public static final double PIVOT_FF = 0;
        public static final double PIVOT_KP = 0;
        public static final double PIVOT_KI = 0;
        public static final double PIVOT_KD = 0;

        // The distance of the pivot from the ground
        public static final double PIVOT_HEIGHT = 21.5;
        // The distance of the pivot from the edge of the frame perimeter
        public static final double PIVOT_DISTANCE = 12;
        // Minimum pivot angle before arm collides with robot (radians) (should be negative)
        public static final double PIVOT_MIN_ANGLE = -0.689; //TODO figure this out for the weird system
        // The base arm length
        public static final double ARM_BASE_LENGTH = 36;
        // The extended arm length
        public static final double ARM_LENGTH_EXT = 144; // TODO: 1/29/2023 figure this out
        // The max height from the floor
        public static final double MAX_LEGAL_HEIGHT = 78 - PIVOT_HEIGHT;
        // The max distance extended from the frame perimeter
        public static final double MAX_LEGAL_DISTANCE = 48 + PIVOT_DISTANCE;
        // The maximum distance the arm will point to
        public static final double MAX_POINT_DISTANCE = MAX_LEGAL_DISTANCE + 100; // TODO: 1/30/2023 this should be the maximum limelight detection distance

        public static final double PIVOT_TICKS_TO_DEGREES = 0;// TODO: 2/1/2023 figure this out so that position things work
        public static final double TELESCOPE_FF = 0;
        public static final double TELESCOPE_KP = 0;
        public static final double TELESCOPE_KI = 0;
        public static final double TELESCOPE_KD = 0;
    }

    public static final class GrabberConstants {
        public static final int CLAW_SOLENOID_CHANNEL = 0;

        public static final int INTAKE_1_ID = 14;
        public static final int INTAKE_2_ID = 15;

        public static final double INTAKE_CURRENT_THRESH = 25;
    }
}