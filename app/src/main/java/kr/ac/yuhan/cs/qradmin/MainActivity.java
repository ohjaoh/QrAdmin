package kr.ac.yuhan.cs.qradmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.ac.yuhan.cs.qradmin.adapter.MemberAdapter;
import kr.ac.yuhan.cs.qradmin.adapter.ProductAdapter;
import kr.ac.yuhan.cs.qradmin.data.MemberData;
import kr.ac.yuhan.cs.qradmin.util.ChangeMode;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

public class MainActivity extends AppCompatActivity {
    // Current Mode Value
    private int mode = 0;

    // MainActivity CardView
    private NeumorphCardView mainCardView;
    private NeumorphCardView footer_menu;

    // Footer Bar Menu
    private NeumorphImageView homeBtn;
    private NeumorphImageView memberBtn;
    private NeumorphImageView productBtn;
    private NeumorphImageView payHistoryBtn;
    private NeumorphImageView productPushBtn;

    // Member Menu
    private NeumorphCardView input_searchId;
    private NeumorphButton memberSearchBtn;
    private NeumorphCardView memberListCardView;

    // HOME Menu
    private NeumorphCardView adminBtn;
    private NeumorphCardView adminScheduleBtn;
    private NeumorphCardView callBtn;
    private NeumorphCardView login;

    // HOME Menu Image
    private ImageView adminSchedule;
    private ImageView adminList;
    private ImageView adminLogin;
    private ImageView call;

    // PaymentList Menu
    private NeumorphButton paySearchBtn;
    private NeumorphCardView payListCardView;
    private NeumorphCardView input_searchIdPay;

    // ProductRegister Menu
    private NeumorphCardView input_productImage;
    private NeumorphCardView input_productName;
    private NeumorphCardView input_productQuantity;
    private NeumorphCardView input_productCategory;
    private NeumorphCardView input_productPrice;
    private NeumorphButton createQRBtn;
    private NeumorphButton createProductBtn;

    // Header ButtonImage
    private NeumorphImageView setting;
    private NeumorphImageView changeMode;

    // Basic BackgroundColor
    private int backgroundColor;
    private int mainBackgroundColor = Color.rgb(236, 240, 243);
    private final int darkModeBackgroundColor = Color.rgb(97, 97, 97);
    private final int btnColor = Color.rgb(0, 174, 142);

    //오자현추가부분
    //검색구현으로 추가한 부분
    private EditText productSearchedittext;
    private NeumorphButton productSearchBtn;
    private String currentSearchText = ""; // 현재 검색 텍스트 저장
    //검색구현으로 추가한부분끝

    private ViewFlipper vFlipper;
    private FirebaseFirestore dbFirestore;
    private ImageView imageViewProduct;
    private Uri fileuri = null;
    private static final int PICK_FILE_REQUEST = 2; // 파일 선택을 위한 요청 코드
    private EditText editProductName, editProductPrice, editProductStock;
    private String ProductCategory;
    private RadioGroup radioGroup;
    private ListView listView2;
    private ProductAdapter adapter2;
    private Handler handler = new Handler();
    private Runnable runnable;

    private ArrayList<Product> productList = new ArrayList<>(); // 상품 정보를 담을 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String categorystr1 = getString(R.string.product_category1);
        String categorystr2 = getString(R.string.product_category2);
        String categorystr3 = getString(R.string.product_category3);

        // Listview Setting
        ListView listView = findViewById(R.id.listView);

        // Create Fake Data
        ArrayList<MemberData> fakeDataList = createFakeData();

        // MemberAdapter Setting
        MemberAdapter adapter = new MemberAdapter(this, fakeDataList);
        listView.setAdapter(adapter);

        // 오자현추가부분
        listView2 = findViewById(R.id.listViewProducts); // 레이아웃에서 리스트 뷰 연결

        radioGroup = findViewById(R.id.categroryRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.categoryRadioBtn1) {
                    ProductCategory = categorystr1;
                } else if (checkedId == R.id.categoryRadioBtn2) {
                    ProductCategory = categorystr2;
                } else if (checkedId == R.id.categoryRadioBtn3) {
                    ProductCategory = categorystr3;
                }
            }
        });
        //카테고리를 클릭하지 않고 넘기는 경우 기본값으로 지정ㅇ
        if (ProductCategory == null) {
            ProductCategory = categorystr1;
        }

        adapter2 = new ProductAdapter(this, productList);
        listView2.setAdapter(adapter2); // 리스트 뷰에 어댑터 설정

        loadItemsFromFirestore();
        imageViewProduct = findViewById(R.id.imageViewProduct);
        imageViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileManager();
            }
        });
        editProductName = findViewById(R.id.editProductName);
        editProductPrice = findViewById(R.id.editProductPrice);
        editProductStock = findViewById(R.id.editProductStock);
        dbFirestore = FirebaseFirestore.getInstance();

        //상품검색기능추가부분시작
        // 상품 목록 자동 새로고침 시작
        startAutoRefresh();
        productSearchedittext = findViewById(R.id.productSearchEditText);
        productSearchBtn = findViewById(R.id.productSearchBtn);
        productSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = productSearchedittext.getText().toString();
                productSearchedittext.setText("");
                currentSearchText = searchText; // 현재 검색 텍스트 업데이트
                loadItemsFromFirestore(); // 필터링된 상품 새로고침
                currentSearchText="";
            }
        });
        //상품검색기능추가부분끝


        // ViewFlipper Setting
        vFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);

        // Main Layout Setting
        LinearLayout main = findViewById(R.id.main);

        // Basic Background Color Setting
        backgroundColor = mainBackgroundColor;
        main.setBackgroundColor(backgroundColor);

        Drawable backgroundDrawable = main.getBackground();
        mainBackgroundColor = ((ColorDrawable) backgroundDrawable).getColor();

        // MainActivity Header Id
        setting = (NeumorphImageView) findViewById(R.id.setting);
        changeMode = (NeumorphImageView) findViewById(R.id.darkMode);

        // Admin MainPage ImageView Id
        adminList = (ImageView) findViewById(R.id.adminList);
        call = (ImageView) findViewById(R.id.call);
        adminLogin = (ImageView) findViewById(R.id.adminLogin);
        adminSchedule = (ImageView) findViewById(R.id.adminSchedule);

        // MainActivity CardView & Footer Id
        mainCardView = (NeumorphCardView) findViewById(R.id.mainCardView);
        footer_menu = (NeumorphCardView) findViewById(R.id.footer_menu);

        // User Management Page Id
        input_searchId = (NeumorphCardView) findViewById(R.id.input_searchId);
        memberSearchBtn = (NeumorphButton) findViewById(R.id.memberSearchBtn);
        memberListCardView = (NeumorphCardView) findViewById(R.id.memberListCardView);

        // Payment List Id
        paySearchBtn = (NeumorphButton) findViewById(R.id.paySearchBtn);
        payListCardView = (NeumorphCardView) findViewById(R.id.payListCardView);
        input_searchIdPay = (NeumorphCardView) findViewById(R.id.input_searchIdPay);

        // Product Register Page Id
        input_productImage = (NeumorphCardView) findViewById(R.id.input_productImage);
        input_productName = (NeumorphCardView) findViewById(R.id.input_productName);
        input_productQuantity = (NeumorphCardView) findViewById(R.id.input_productQuantity);
        input_productCategory = (NeumorphCardView) findViewById(R.id.input_productCategory);
        input_productPrice = (NeumorphCardView) findViewById(R.id.input_productPrice);
        createQRBtn = (NeumorphButton) findViewById(R.id.createQRBtn);
        createProductBtn = (NeumorphButton) findViewById(R.id.createProductBtn);
        memberSearchBtn = (NeumorphButton) findViewById(R.id.memberSearchBtn);

        // Footer Menu Icon Id
        memberBtn = (NeumorphImageView) findViewById(R.id.memberBtn);
        productBtn = (NeumorphImageView) findViewById(R.id.productBtn);
        homeBtn = (NeumorphImageView) findViewById(R.id.homeBtn);
        payHistoryBtn = (NeumorphImageView) findViewById(R.id.payHistoryBtn);
        productPushBtn = (NeumorphImageView) findViewById(R.id.productPushBtn);

        // MainActivity Button BackgroundColor Setting
        createQRBtn.setBackgroundColor(btnColor);
        createProductBtn.setBackgroundColor(btnColor);
        memberSearchBtn.setBackgroundColor(btnColor);
        paySearchBtn.setBackgroundColor(btnColor);

        // MainActivity Home Menu CardView Id
        login = (NeumorphCardView) findViewById(R.id.login);
        adminBtn = (NeumorphCardView) findViewById(R.id.adminBtn);
        adminScheduleBtn = (NeumorphCardView) findViewById(R.id.adminScheduleBtn);
        callBtn = (NeumorphCardView) findViewById(R.id.callBtn);

        // ListView Item onClickListener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get Information of Clicked Member Item
                MemberData selectedItem = fakeDataList.get(position);
                showMemberInfoDialog(selectedItem);
            }
        });

        // SettingBtn onClickListener
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting.setShapeType(1);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setting.setShapeType(0);
                    }
                }, 200);
                // Setting 페이지로 이동 및 메인페이지 배경색상 전달
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                intent.putExtra("background_color", backgroundColor);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        });

        // ChangeModeBtn onClickListener
        changeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode.setShapeType(1);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeMode.setShapeType(0);
                    }
                }, 200);

                if (mode == 0) {
                    // DarkMode
                    backgroundColor = darkModeBackgroundColor;
                    main.setBackgroundColor(backgroundColor);

                    // Change FontColor
                    // Find and Change The Color Of All TextViews In Every Root view
                    ChangeMode.applyMainTheme(main, mode);

                    ChangeMode.setDarkShadowCardView(mainCardView);
                    ChangeMode.setDarkShadowCardView(footer_menu);

                    // MemberManagement Page CardView
                    ChangeMode.setDarkShadowCardView(input_searchId);
                    ChangeMode.setDarkShadowCardView(memberSearchBtn);
                    ChangeMode.setDarkShadowCardView(memberListCardView);

                    // AdminMain Page CardView
                    ChangeMode.setDarkShadowCardView(adminBtn);
                    ChangeMode.setDarkShadowCardView(adminScheduleBtn);
                    ChangeMode.setDarkShadowCardView(callBtn);
                    ChangeMode.setDarkShadowCardView(login);

                    // PaymentList Page CardView
                    ChangeMode.setColorFilterLight(paySearchBtn);
                    ChangeMode.setDarkShadowCardView(paySearchBtn);
                    ChangeMode.setDarkShadowCardView(payListCardView);
                    ChangeMode.setDarkShadowCardView(input_searchIdPay);

                    // ProductRegister Page CardView
                    ChangeMode.setDarkShadowCardView(input_productImage);
                    ChangeMode.setDarkShadowCardView(input_productName);
                    ChangeMode.setDarkShadowCardView(input_productQuantity);
                    ChangeMode.setDarkShadowCardView(input_productCategory);
                    ChangeMode.setDarkShadowCardView(input_productPrice);
                    ChangeMode.setDarkShadowCardView(createQRBtn);
                    ChangeMode.setDarkShadowCardView(createProductBtn);

                    // Change ImageView Color
                    ChangeMode.setColorFilterDark(adminList);
                    ChangeMode.setColorFilterDark(call);
                    ChangeMode.setColorFilterDark(adminLogin);
                    ChangeMode.setColorFilterDark(adminSchedule);

                    // Footer Menu
                    ChangeMode.setColorFilterDark(memberBtn);
                    ChangeMode.setDarkShadowCardView(memberBtn);
                    ChangeMode.setColorFilterDark(productBtn);
                    ChangeMode.setDarkShadowCardView(productBtn);
                    ChangeMode.setColorFilterDark(homeBtn);
                    ChangeMode.setDarkShadowCardView(homeBtn);
                    ChangeMode.setColorFilterDark(payHistoryBtn);
                    ChangeMode.setDarkShadowCardView(payHistoryBtn);
                    ChangeMode.setColorFilterDark(productPushBtn);
                    ChangeMode.setDarkShadowCardView(productPushBtn);
                    ChangeMode.setColorFilterDark(setting);
                    ChangeMode.setDarkShadowCardView(setting);

                    // Change ChangeMode Image & Color
                    changeMode.setImageResource(R.drawable.light);
                    ChangeMode.setColorFilterDark(changeMode);
                    ChangeMode.setDarkShadowCardView(changeMode);

                    // Change Mode Value
                    mode++;
                } else if (mode == 1) {
                    // LightMode
                    backgroundColor = mainBackgroundColor;
                    main.setBackgroundColor(backgroundColor);

                    // Change FontColor
                    // Find and Change The Color Of All TextViews In Every Root view
                    ChangeMode.applyMainTheme(main, mode);

                    ChangeMode.setLightShadowCardView(footer_menu);
                    ChangeMode.setLightShadowCardView(mainCardView);

                    // MemberManagement Page CardView
                    ChangeMode.setLightShadowCardView(input_searchId);
                    ChangeMode.setLightShadowCardView(memberSearchBtn);
                    ChangeMode.setLightShadowCardView(memberListCardView);

                    // AdminMain Page CardView
                    ChangeMode.setLightShadowCardView(adminBtn);
                    ChangeMode.setLightShadowCardView(adminScheduleBtn);
                    ChangeMode.setLightShadowCardView(callBtn);
                    ChangeMode.setLightShadowCardView(login);

                    // PaymentList Page CardView
                    ChangeMode.setColorFilterLight(paySearchBtn);
                    ChangeMode.setLightShadowCardView(paySearchBtn);
                    ChangeMode.setLightShadowCardView(payListCardView);
                    ChangeMode.setLightShadowCardView(input_searchIdPay);

                    // ProductRegister Page CardView
                    ChangeMode.setLightShadowCardView(input_productImage);
                    ChangeMode.setLightShadowCardView(input_productName);
                    ChangeMode.setLightShadowCardView(input_productQuantity);
                    ChangeMode.setLightShadowCardView(input_productPrice);
                    ChangeMode.setLightShadowCardView(input_productCategory);
                    ChangeMode.setLightShadowCardView(createQRBtn);
                    ChangeMode.setLightShadowCardView(createProductBtn);

                    // Change ImageView Color
                    ChangeMode.setColorFilterLight(adminList);
                    ChangeMode.setColorFilterLight(call);
                    ChangeMode.setColorFilterLight(adminLogin);
                    ChangeMode.setColorFilterLight(adminSchedule);

                    // Footer Menu
                    ChangeMode.setLightShadowCardView(memberBtn);
                    ChangeMode.setColorFilterLight(memberBtn);
                    ChangeMode.setLightShadowCardView(productBtn);
                    ChangeMode.setColorFilterLight(productBtn);
                    ChangeMode.setLightShadowCardView(homeBtn);
                    ChangeMode.setColorFilterLight(homeBtn);
                    ChangeMode.setLightShadowCardView(payHistoryBtn);
                    ChangeMode.setColorFilterLight(payHistoryBtn);
                    ChangeMode.setLightShadowCardView(productPushBtn);
                    ChangeMode.setColorFilterLight(productPushBtn);
                    ChangeMode.setLightShadowCardView(setting);
                    ChangeMode.setColorFilterLight(setting);

                    // Change ChangeMode Image & Color
                    changeMode.setImageResource(R.drawable.dark);
                    ChangeMode.setLightShadowCardView(changeMode);
                    ChangeMode.setColorFilterLight(changeMode);

                    // Change Mode Value
                    mode--;
                } else {
                    showErrorDialog(MainActivity.this, "임성준");
                }
            }
        });

        // AdminBtn onClickListener
        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                adminBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adminBtn.setShapeType(0);
                    }
                }, 200);
                // Move to AdminList Page & transfer main page background color
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                intent.putExtra("background_color", backgroundColor);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        });

        // AdminScheduleBtn onClickListener
        adminScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                adminScheduleBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adminScheduleBtn.setShapeType(0);
                    }
                }, 200);
                // Move to AdminSchedule Page & transfer main page background color
                Intent intent = new Intent(getApplicationContext(), AdminScheduleActivity.class);
                intent.putExtra("background_color", backgroundColor);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        });

        // CallBtn onClickListener
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                callBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callBtn.setShapeType(0);
                    }
                }, 200);

                // Move to Dial
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:/010-1234-1234"));
                startActivity(intent);
            }
        });

        // 오자현 추가부분
        createQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateQR.class);
                startActivity(intent); // CreateQR 액티비티를 시작합니다.
            }
        });


        createProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "서버와 연동으로 인해 약간의 딜레이가 발생할 수 있습니다.", Toast.LENGTH_SHORT).show();
                uploadFileAndSaveProductInfo();

            }
        });

        // LoginBtn onClickListener
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                login.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        login.setShapeType(0);
                    }
                }, 200);
                // Move to Login Page & transfer main page background color
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("background_color", backgroundColor);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        });

        // HomeBtn onClickListener
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                homeBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        homeBtn.setShapeType(0);
                    }
                }, 200);
                vFlipper.setDisplayedChild(0);
            }
        });

        // MemberBtn onClickListener
        memberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                memberBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        memberBtn.setShapeType(0);
                    }
                }, 200);
                vFlipper.setDisplayedChild(1);
            }
        });

        // ProductBtn onClickListener
        productBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                productBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        productBtn.setShapeType(0);
                    }
                }, 200);
                vFlipper.setDisplayedChild(2);
            }
        });

        // PayHistoryBtn onClickListener
        payHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                payHistoryBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        payHistoryBtn.setShapeType(0);
                    }
                }, 200);
                vFlipper.setDisplayedChild(3);
            }
        });

        // ProductPushBtn onClickListener
        productPushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                productPushBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        productPushBtn.setShapeType(0);
                    }
                }, 200);
                vFlipper.setDisplayedChild(4);
            }
        });
    }

    // Create Fake Data
    private ArrayList<MemberData> createFakeData() {
        ArrayList<MemberData> dataList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            // Create Fake Data & Add Lis
            MemberData memberData = new MemberData(i, "Member" + i, new Date(), i * 100);
            dataList.add(memberData);
        }
        return dataList;
    }

    // Error Dialog Method
    public static void showErrorDialog(Context context, String message) {
        // Create Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("오류 발생")
                .setMessage(message)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Pressed "확인" Btn
                        dialog.dismiss(); // Close Dialog
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showMemberInfoDialog(MemberData selectedItem) {
        // Create Dialog & Layout Setting
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_user_item_info);

        // Get TextView ID in Dialog
        TextView textViewMemberId = dialog.findViewById(R.id.textViewUserNum);
        textViewMemberId.setText("Num: " + selectedItem.getNumber());

        TextView textViewMemberName = dialog.findViewById(R.id.textViewUserId);
        textViewMemberName.setText("Name: " + selectedItem.getUserId());

        TextView textViewMemberDate = dialog.findViewById(R.id.textViewUserDate);
        textViewMemberDate.setText("Date: " + selectedItem.getJoinDate().toString());

        TextView textViewMemberAmount = dialog.findViewById(R.id.textViewUserPoint);
        textViewMemberAmount.setText("Point: $" + selectedItem.getPoint());

        // Show Dialog
        dialog.show();
    }
    //오자현 추가부분

    //상품검색기능구현시작
    // 5초마다 새로고침 시작
    private void startAutoRefresh() {
        runnable = new Runnable() {
            @Override
            public void run() {
                loadItemsFromFirestore();
                handler.postDelayed(this, 5000);  // 5초 후에 다시 실행하도록 스케줄링, 일시정지 여부와 상관없이 스케줄 유지
            }
        };
        handler.postDelayed(runnable, 5000);  // 처음 시작
    }


    void loadItemsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> query;

        if (!currentSearchText.isEmpty()) {
            // 검색 텍스트가 있을 경우, 해당 상품 이름으로 필터링된 쿼리 실행
            query = db.collection("products")
                    .whereEqualTo("productName", currentSearchText)
                    .get();
        } else {
            // 검색 텍스트가 없을 경우, 전체 상품 로드
            query = db.collection("products").get();
        }

        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    productList.clear(); // 기존의 리스트를 클리어
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int code = document.getLong("productCode").intValue();
                        String productName = document.getString("productName");
                        String category = document.getString("category");
                        String imageUrl = document.getString("imageUrl");
                        int price = document.getLong("price").intValue();
                        int stock = document.getLong("stock").intValue();

                        if (imageUrl == null || imageUrl.isEmpty()) {
                            imageUrl = "R.drawable.default_image"; // 기본 이미지 URL 사용
                        }

                        productList.add(new Product(code, productName, category, imageUrl, price, stock)); // 리스트에 제품 추가
                    }
                    adapter2.notifyDataSetChanged(); // 데이터 변경을 어댑터에 알림
                } else {
                    Log.e("DatabaseViewActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    //상품검색기능구현끝
    private void uploadFileAndSaveProductInfo() {
        Log.d("UploadFile", "uploadFileAndSaveProductInfo started");
        String name = editProductName.getText().toString().trim();
        String priceStr = editProductPrice.getText().toString().trim();
        String stockStr = editProductStock.getText().toString().trim();
        String category = ProductCategory;

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || fileuri == null || category.isEmpty()) {
            Toast.makeText(this, "모든 필드를 채워주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = Integer.parseInt(priceStr);
        int stock = Integer.parseInt(stockStr);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("files/" + System.currentTimeMillis());

        UploadTask uploadTask = fileRef.putFile(fileuri);
        uploadTask.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("UploadFile", "파이어베이스업로드리스너 진입");
            String fileUrl = uri.toString();

            // Firestore에서 productCounter 문서를 업데이트하고 새 productCode를 가져옵니다.
            // Firestore에서 counters 컬렉션을 만들고  productCounter 문서를 직접 생성한다.
            // lastProductCode 필드에 초기값(예: 0)을 설정합니다. 데이터타입은 number 이 문서가 없으면 프로그램이 진행 안됨
            // 만약에 상품컬렉션을 지웠으면 이거도 관리해서 0으로 만들것(수동임)
            DocumentReference counterRef = dbFirestore.collection("counters").document("productCounter");
            dbFirestore.runTransaction(transaction -> {
                DocumentSnapshot counterSnapshot = transaction.get(counterRef);
                Long lastProductCode = counterSnapshot.getLong("lastProductCode");
                if (lastProductCode == null) lastProductCode = 0L; // 초기값 설정
                Long newProductCode = lastProductCode + 1;
                transaction.update(counterRef, "lastProductCode", newProductCode);

                // 상품 정보와 파일 URL을 Firestore에 저장합니다.
                Map<String, Object> product = new HashMap<>();
                product.put("productName", name);
                product.put("imageUrl", fileUrl);
                product.put("price", price);
                product.put("stock", stock);
                product.put("category", category);
                product.put("productCode", newProductCode); // 새로운 productCode 사용

                dbFirestore.collection("products").add(product).addOnSuccessListener(documentReference -> {
                    Toast.makeText(MainActivity.this, "상품 정보와 파일 URL 파이어베이스에 저장 성공", Toast.LENGTH_SHORT).show();
                    finishActivityWithResult();
                }).addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "파이어베이스 저장 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

                return null;
            }).addOnFailureListener(e -> {
                Toast.makeText(MainActivity.this, "상품 코드 업데이트 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        })).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "파일 업로드 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    // 파이어베이스에 업로드 후처리를 위한 메서드(여기선 홈으로 보내고 내용을 초기화함)
    private void finishActivityWithResult() {
        vFlipper.setDisplayedChild(0);
        imageViewProduct.setImageResource(android.R.drawable.ic_menu_camera);
        editProductName.setText("");
        editProductPrice.setText("");
        editProductStock.setText("");
        radioGroup.check(R.id.categoryRadioBtn1);
    }

    private void openFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // 모든 유형의 파일을 허용
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            fileuri = data.getData();
            // ImageView에 이미지 로드
            imageViewProduct.setImageURI(selectedFileUri);
        }
    }
}