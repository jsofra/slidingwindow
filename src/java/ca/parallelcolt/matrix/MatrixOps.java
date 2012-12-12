package ca.parallelcolt.matrix;

import cern.colt.matrix.tint.IntMatrix2D;
import ca.parallelcolt.matrix.Int9Function;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.function.tdouble.Double9Function;

import cern.colt.matrix.tdcomplex.DComplexMatrix2D;
import ca.parallelcolt.matrix.DComplex4Function;

public class MatrixOps {

    public static void zAssign8Neighbors(DoubleMatrix2D A, DoubleMatrix2D B, Double9Function function) {
        A.zAssign8Neighbors(B, function);
    }

    public static void zAssign8Neighbors(IntMatrix2D A, IntMatrix2D B, Int9Function function) {
        int r = A.rows() - 1;
        int c = A.columns() - 1;
        int a00, a01, a02;
        int a10, a11, a12;
        int a20, a21, a22;
        for (int i = 1; i < r; i++) {
            a00 = A.getQuick(i - 1, 0);
            a01 = A.getQuick(i - 1, 1);
            a10 = A.getQuick(i, 0);
            a11 = A.getQuick(i, 1);
            a20 = A.getQuick(i + 1, 0);
            a21 = A.getQuick(i + 1, 1);

            for (int j = 1; j < c; j++) {
                // in each step six cells can be remembered in registers - they
                // don't need to be reread from slow memory
                // in each step 3 instead of 9 cells need to be read from
                // memory.
                a02 = A.getQuick(i - 1, j + 1);
                a12 = A.getQuick(i, j + 1);
                a22 = A.getQuick(i + 1, j + 1);

                B.setQuick(i, j, function.apply(a00, a01, a02, a10, a11, a12, a20, a21, a22));

                a00 = a01;
                a10 = a11;
                a20 = a21;

                a01 = a02;
                a11 = a12;
                a21 = a22;
            }
        }
    }

    /*
    public static void zAssign4Neighbors(DComplex4Function function, DoubleMatrix2D ... mats) {
        DoubleMatrix2D A = mats[0];
        int r = A.rows() - 1;
        int c = A.columns() - 1;
        int a01, a10, a11, a12, a21;
        for (int i = 1; i < r; i++) {
            a10 = A.getQuick(i, 0);
            a11 = A.getQuick(i, 1);

            for (int j = 1; j < c; j++) {
                a01 = A.getQuick(i - 1, j);
                a12 = A.getQuick(i, j + 1);
                a21 = A.getQuick(i + 1, j);

                B.setQuick(i, j, function.apply(a01, a10, a11, a12, a21));

                a10 = a11;
                a11 = a12;
            }
        }
    }
    */

}
