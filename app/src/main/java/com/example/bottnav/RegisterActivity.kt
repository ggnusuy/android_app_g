package com.example.bottnav

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi

class RegisterActivity : AppCompatActivity() {
    // 회원가입 액티비티

    private lateinit var register_editNickname: EditText
    private lateinit var register_editId: EditText
    private lateinit var register_btnDouCheck: Button
    private lateinit var register_editPwd: EditText
    private lateinit var register_editPwdcheck: EditText
    private lateinit var register_btnRegister: Button
    lateinit var dbManager: DBManager
    private lateinit var dbHelper: DBHelper
    private lateinit var sqlDB: SQLiteDatabase
    private lateinit var cursor: Cursor

    lateinit var nickname: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var passwordCheck: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_editNickname = findViewById(R.id.register_editNickname)
        register_editId = findViewById(R.id.register_editId)
        register_btnDouCheck = findViewById(R.id.register_btnDouCheck)
        register_editPwd = findViewById(R.id.register_editPwd)
        register_editPwdcheck = findViewById(R.id.register_editPwdcheck)
        register_btnRegister = findViewById(R.id.register_btnRegister)

        dbHelper = DBHelper(this)
        dbManager = DBManager(this)


        //중복 확인 코드
        register_btnDouCheck.setOnClickListener {
            email = register_editId.text.toString()
            sqlDB = dbHelper.writableDatabase
            //20자리까지만 입력 가능
            if (email.length > 20) Toast.makeText(this, "이메일은 20자리까지 가능합니다. ", Toast.LENGTH_SHORT)
                .show()
            else {
                cursor = sqlDB.rawQuery("SELECT email FROM USERS WHERE email=\'$email\';", null)
                if (cursor.count != 0) {  //이미 존재하는 회원인 경우
                    Toast.makeText(this, "이미 가입된 회원입니다.", Toast.LENGTH_SHORT).show()
                    register_editId.text = null //이메일 초기화
                } else {  //20자리를 넘지 않고+존재하지 않는 회원인 경우
                    Toast.makeText(this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show()

                    //등록 버튼 클릭시
                    register_btnRegister.setOnClickListener {
                        nickname = register_editNickname.text.toString()
                        password = register_editPwd.text.toString()
                        passwordCheck = register_editPwdcheck.text.toString()
                        //비어있는 칸이 있는지 확인
                        if (email.isEmpty() || nickname.isEmpty() || password.isEmpty()) {
                            Toast.makeText(this, "빈 칸을 채워주세요.", Toast.LENGTH_SHORT).show()
                        } else if (nickname.length > 15) {
                            Toast.makeText(this, "닉네임은 15자리까지 가능합니다. ", Toast.LENGTH_SHORT).show()
                        } else {
                            //비밀번호 같은지 확인
                            if (password == passwordCheck) {
                                //회원가입 코드
                                dbManager.newUser(email, nickname, password)
                                Toast.makeText(this, "회원 가입 성공", Toast.LENGTH_SHORT).show()
                                //db저장 완료된 상태. 로그인액티비티로 이동
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.putExtra("email", email)
                                intent.putExtra("password", password)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "비밀번호가 같지 않습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
        //중복확인 버튼 누르기 전 회원가입하기 버튼 클릭 시
        register_btnRegister.setOnClickListener {
            Toast.makeText(this, "중복 확인을 먼저 해주세요", Toast.LENGTH_SHORT).show()
        }

    }
}