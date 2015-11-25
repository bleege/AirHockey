package com.bradleege.airhockey.util;

import android.util.Log;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;

public class ShaderHelper {

    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String vertextShaderSourceCode) {
        return compileShader(GL_VERTEX_SHADER, vertextShaderSourceCode);
    }

    public static int compileFragmentShader(String fragmentShaderSourceCode) {
        return compileShader(GL_FRAGMENT_SHADER, fragmentShaderSourceCode);
    }

    private static int compileShader(int type, String shaderSourceCode) {

        // Id for OpenGL Shader Object
        final int shaderObjectId = glCreateShader(type);

        if (shaderObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new shader.");
            }
            return 0;
        }

        // Load Shader Source Code Into OpenGL Shader Object referenced by shaderObjectId
        glShaderSource(shaderObjectId, shaderSourceCode);

        // Compile the Shader Source Code loaded into the OpenGL Shader object in previous step
        glCompileShader(shaderObjectId);

        // Retrieve results of Shader Compilation from Shader Object.
        // Place result in int array (compileStatus) at index specified (0 in this case)
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);


        if (LoggerConfig.ON) {
            // Display results of shader compilation
            Log.v(TAG, "Results of compiling shader source:\n" + shaderSourceCode + "\n" + glGetShaderInfoLog(shaderObjectId));
        }

        // Check for compilation failure
        if (compileStatus[0] == 0) {
            // Compilation failed, clean up shader memory
            glDeleteShader(shaderObjectId);

            if (LoggerConfig.ON) {
                Log.w(TAG, "Compilation of shader failed.");
            }
            return 0;
        }

        // Compilation didn't fail, so return shader object id
        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {

        final int programObjectId = glCreateProgram();

        // Check that a program was created
        if (programObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new OpenGL program.");
            }
            return 0;
        }

        // Attached shaders to program
        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);

        // Link the shaders in the program
        glLinkProgram(programObjectId);

        // Check that the program linking worked
        final int linkStatus[]  = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        if (LoggerConfig.ON) {
            Log.v(TAG, "Results of linking program:\n" + glGetProgramInfoLog(programObjectId));
        }

        if (linkStatus[0] == 0) {
            // Linking failed, so cleanup program memory
            glDeleteProgram(programObjectId);

            if (LoggerConfig.ON) {
                Log.w(TAG, "Linking of program failed.");
            }

            return 0;
        }

        // Return linked program with attached shaders
        return programObjectId;
    }
}
