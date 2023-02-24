package org.team1540.robot2023.commands.drivetrain;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.kauailabs.navx.frc.AHRS;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.team1540.robot2023.Constants;
import org.team1540.robot2023.utils.swerve.SwerveModule;

import java.util.Objects;

import static org.team1540.robot2023.Constants.Swerve;
import static org.team1540.robot2023.Globals.field2d;
import static org.team1540.robot2023.Globals.frontLimelight;

public class Drivetrain extends SubsystemBase {

    private SwerveModuleState[] states = new SwerveModuleState[]{new SwerveModuleState(), new SwerveModuleState(), new SwerveModuleState(), new SwerveModuleState()};
    private final SwerveModule[] modules = new SwerveModule[]{
            new SwerveModule(0, Swerve.Mod0.constants),
            new SwerveModule(1, Swerve.Mod1.constants),
            new SwerveModule(2, Swerve.Mod2.constants),
            new SwerveModule(3, Swerve.Mod3.constants)
    };

    private final AHRS gyro = new AHRS(SPI.Port.kMXP);

    // Whether to allow the wheels to park
    private boolean isParkMode = false;

    // Odometry
    private final SwerveDrivePoseEstimator poseEstimator = new SwerveDrivePoseEstimator(Swerve.swerveKinematics, getYaw(), getModulePositions(), new Pose2d());

    public Drivetrain() {
        SmartDashboard.setDefaultNumber("drivetrain/translate/kp", Constants.Auto.PID.translationP);
        SmartDashboard.setDefaultNumber("drivetrain/translate/ki", Constants.Auto.PID.translationI);
        SmartDashboard.setDefaultNumber("drivetrain/translate/kd", Constants.Auto.PID.translationD);
        SmartDashboard.setDefaultNumber("drivetrain/rotate/kp", Constants.Auto.PID.rotationP);
        SmartDashboard.setDefaultNumber("drivetrain/rotate/ki", Constants.Auto.PID.rotationI);
        SmartDashboard.setDefaultNumber("drivetrain/rotate/kd", Constants.Auto.PID.rotationD);

        gyro.reset();
    }

    @Override
    public void periodic() {

        SwerveDriveKinematics.desaturateWheelSpeeds(states, Swerve.maxVelocity);
        modules[0].setDesiredState(states[0], true, isParkMode);
        modules[1].setDesiredState(states[1], true, isParkMode);
        modules[2].setDesiredState(states[2], true, isParkMode);
        modules[3].setDesiredState(states[3], true, isParkMode);
        poseEstimator.update(getYaw(), getModulePositions());
        Pose2d rawBotPose = frontLimelight.getBotPose();
        Pose2d filteredBotPose = frontLimelight.getFilteredBotPose();
        if (filteredBotPose != null) {
            poseEstimator.addVisionMeasurement(filteredBotPose,  edu.wpi.first.wpilibj.Timer.getFPGATimestamp()-(frontLimelight.getDeltaTime()/1000));
            field2d.getObject("VisionPoseFiltered").setPose(filteredBotPose);
        } else {
            field2d.getObject("VisionPoseFiltered").setPose(new Pose2d());
        }
        field2d.getObject("VisionPoseReal").setPose(Objects.requireNonNullElseGet(rawBotPose, Pose2d::new));

        field2d.setRobotPose(poseEstimator.getEstimatedPosition());

    }


    public void resetAllToAbsolute() {
        System.out.println("RESETTING ALL");
        for (SwerveModule module: modules) {
            module.resetToAbsolute();
        }
    }

    /**
     * Adjusts all the wheels to achieve the desired movement
     *
     * @param xPercent      The forward and backward movement
     * @param yPercent      The left and right movement
     * @param rotPercent           The amount to turn
     * @param fieldRelative If the directions are relative to the field instead of the robot
     */
    public void drive(double xPercent, double yPercent, double rotPercent, boolean fieldRelative) {

        double xSpeed = xPercent * Swerve.maxVelocity;
        double ySpeed = yPercent * Swerve.maxVelocity;
        double rot = Math.toRadians(rotPercent*360);
        ChassisSpeeds chassisSpeeds = fieldRelative
                ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rot, gyro.getRotation2d())
                : new ChassisSpeeds(xSpeed, ySpeed, rot);
        double deadzone = 0.02;
        double rotDeadzone = 0.1;
        if (Math.abs(xPercent) > 0 || Math.abs(yPercent) > deadzone || Math.abs(rot) > rotDeadzone) {
            isParkMode = false;
            setChassisSpeeds(chassisSpeeds);
        } else {
            stopLocked();
        }
    }

    /**
     * Stops the robot and forms an X with the wheels
     */
    public void stopLocked() {
        isParkMode = true;
        setModuleStates(new SwerveModuleState[]{
                new SwerveModuleState(0, Rotation2d.fromDegrees(45)), //Front Left
                new SwerveModuleState(0, Rotation2d.fromDegrees(-45)), //Front Right
                new SwerveModuleState(0, Rotation2d.fromDegrees(-45)), //Back Left
                new SwerveModuleState(0, Rotation2d.fromDegrees(45)) //Back Right
        });
    }

    void setModuleStates(SwerveModuleState[] newStates) {
        this.states = newStates;
    }

    private void setChassisSpeeds(ChassisSpeeds speeds) {
        states = Swerve.swerveKinematics.toSwerveModuleStates(speeds);
    }


    protected Command getPathCommand(PathPlannerTrajectory trajectory) {
        return new PPSwerveControllerCommand(
                trajectory,
                this::getPose, // Pose supplier
                // TODO: Tune
                new PIDController(SmartDashboard.getNumber("drivetrain/translate/kp",Constants.Auto.PID.translationP),SmartDashboard.getNumber("drivetrain/translate/ki",Constants.Auto.PID.translationI),SmartDashboard.getNumber("drivetrain/translate/kd",Constants.Auto.PID.translationD)), // X controller. Tune these values for your robot. Leaving them 0 will only use feedforwards.
                new PIDController(SmartDashboard.getNumber("drivetrain/translate/kp",Constants.Auto.PID.translationP),SmartDashboard.getNumber("drivetrain/translate/ki",Constants.Auto.PID.translationI),SmartDashboard.getNumber("drivetrain/translate/kd",Constants.Auto.PID.translationD)), // Y controller. Should be same as values for X controller
                new PIDController(SmartDashboard.getNumber("drivetrain/rotate/kp",Constants.Auto.PID.rotationP),SmartDashboard.getNumber("drivetrain/rotate/ki",Constants.Auto.PID.rotationI),SmartDashboard.getNumber("drivetrain/rotate/kd",Constants.Auto.PID.rotationD)), // Rotation controller. Tune these values for your robot. Leaving them 0 will only use feedforwards
                this::setChassisSpeeds, // Module states consumer
                this // Requires this drive subsystem
        );
    }

    protected Command getResettingPathCommand(PathPlannerTrajectory trajectory) {
        return new SequentialCommandGroup(
                new InstantCommand(() -> resetOdometry(trajectory.getInitialHolonomicPose())),
                getPathCommand(trajectory)
        );
    }


    /**
     * Sets the gyroscope angle to zero. This can be used to set the direction the robot is currently facing to the
     * 'forwards' direction.
     */
    public void zeroGyroscope() {
        gyro.zeroYaw();
    } //todo: make sure this doesn't break odometry

    public Rotation2d getYaw() {
        if (gyro.isMagnetometerCalibrated()) {
            // We will only get valid fused headings if the magnetometer is calibrated
            return Rotation2d.fromDegrees(gyro.getFusedHeading());
        }
        // We have to invert the angle of the NavX so that rotating the robot counter-clockwise makes the angle increase.
        return Rotation2d.fromDegrees(360.0 - gyro.getYaw());
    }

    public Rotation2d getPitch() {
        return Rotation2d.fromDegrees(gyro.getPitch());
    }

    public void setNeutralMode(NeutralMode neutralMode) {
        for (SwerveModule module : modules) {
            module.setNeutralMode(neutralMode);
        }
    }
    public Rotation2d getRoll() {
        return Rotation2d.fromDegrees(gyro.getRoll());
    }

    public Pose2d getPose() {
        return poseEstimator.getEstimatedPosition();
    }

    public void resetOdometry(Pose2d pose) {
        poseEstimator.resetPosition(getYaw(), getModulePositions(), pose);
    }



    public SwerveModulePosition[] getModulePositions(){
        return new SwerveModulePosition[]{
                modules[0].getPosition(),
                modules[1].getPosition(),
                modules[2].getPosition(),
                modules[3].getPosition()
        };
    }

}
