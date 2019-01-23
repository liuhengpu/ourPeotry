package com.rednow.poetry.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.facebook.stetho.common.LogUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.rednow.poetry.R;
import com.rednow.poetry.adapter.section.PoetryDetailBottomSelection;
import com.rednow.poetry.adapter.section.PoetryDetailHeaderSelection;
import com.rednow.poetry.adapter.section.PoetryDetailItemSelection;
import com.rednow.poetry.api.RetrofitHelper;
import com.rednow.poetry.base.RxBaseActivity;
import com.rednow.poetry.entity.PoetryDetail;
import com.rednow.poetry.utils.CommonUtil;
import com.rednow.poetry.widget.CustomEmptyView;
import com.rednow.poetry.widget.SpaceItemDecoration;
import com.rednow.poetry.widget.sectioned.SectionedRecyclerViewAdapter;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by SnowDragon2015
 * <p>
 * 2017/9/13
 * <p>
 * Github ：https://github.com/SnowDragon2015
 */
public class PoetryDetailActivity extends RxBaseActivity {

    private static final String EXT_INFO = "ext_info";
    private static final String EXT_SELECTED_STR = "ext_select";

    @BindView(R.id.title_bar_name)
    TextView title_bar_name;

    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;

    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;

    private SectionedRecyclerViewAdapter mSectionedRecyclerViewAdapter;

    private int id;
    private String selectedStr;
    private String[] mPersonValues;
    public static void startIntent(Context context, int id,String extStr) {
        Intent intent = new Intent(context, PoetryDetailActivity.class);
        intent.putExtra(EXT_INFO, id);
        intent.putExtra(EXT_SELECTED_STR,extStr);
        context.startActivity(intent);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_poetry_detail;
    }

    @Override
    protected void initView(Bundle savedInstancedState) {
        id = getIntent().getIntExtra(EXT_INFO, 0);
        selectedStr = getIntent().getStringExtra(EXT_SELECTED_STR);

        mSectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new SpaceItemDecoration(CommonUtil.dpToPx(this,10)));

        mSectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        recyclerView.setAdapter(mSectionedRecyclerViewAdapter);

        mPersonValues = getResources().getStringArray(R.array.voicer_cloud_entries);
        initXunfei();

        loadData();

    }
    private PoetryDetailHeaderSelection        poetryDetailHeaderSelection;
    private void loadData() {
        RetrofitHelper.getPoetryApi().getPoetryDetail(id)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(poetryDetail -> {
                    hideEmptyView();
                    if (poetryDetail != null && poetryDetail.getShiwens() != null && poetryDetail.getShiwens().size() > 0) {
                        PoetryDetail.Poetry poetry = poetryDetail.getShiwens().get(0);
                        title_bar_name.setText(poetry.getNameStr());
                        poetryDetailHeaderSelection =    new PoetryDetailHeaderSelection(this, poetry,selectedStr);
                        poetryDetailHeaderSelection.setInterface(new PoetryDetailHeaderSelection.headItemCallBack() {
                            @Override
                            public void doread(String readstring) {

                                toast("click"+readstring);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        playClick("画，王维，唐代，远看山有色，近听水无声。春去花还在，人来鸟不惊。");
                                    }
                                });

                            }
                        });
                         mSectionedRecyclerViewAdapter.addSection(poetryDetailHeaderSelection);

                        if (poetry.getInfo()!=null && poetry.getInfo().size()>0){
                            mSectionedRecyclerViewAdapter.addSection(new PoetryDetailItemSelection(poetry.getInfo()));
                        }

                        if (poetry.getInfo_author() != null){
                            mSectionedRecyclerViewAdapter.addSection(new PoetryDetailBottomSelection(poetry.getInfo_author()));
                        }

                    } else initEmptyView("暂无数据");

                }, throwable -> {
                    initEmptyView("加载失败，请检查网络连接");
                });

    }


    @OnClick(R.id.title_bar_back)
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_back:
                finished();
                break;
        }
    }

    public void initEmptyView(String string) {

        mCustomEmptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        if (string == null)
            mCustomEmptyView.setEmptyText("加载失败~(≧▽≦)~啦啦啦.");
        else mCustomEmptyView.setEmptyText(string);
    }

    public void hideEmptyView() {


        mCustomEmptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finished();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void finished() {
        this.finish();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    /**
     * 点击播放
     */
    private SpeechSynthesizer mTts;
    private SharedPreferences mSharedPreferences;
    private void initXunfei() {
        // 初始化合成对象
         mTts = SpeechSynthesizer.createSynthesizer(this,mTtsInitListener);
         mSharedPreferences = getSharedPreferences(PlaySetting.PREFER_NAME, MODE_PRIVATE);
    }
    //设置语音参数
    public void setParam() {
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人,可在values/string.xml中选择合适的人的声音
        mTts.setParameter(SpeechConstant.VOICE_NAME, mPersonValues[Integer.valueOf(mSharedPreferences.getString("person_preference", "0"))]);
        //设置合成语速,可设置(0-100)

        mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
        //设置合成音调,可设置(0-100)
        mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
        //设置合成音量,可设置(0-100)
        mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
    }
    /**
     * 初始化监听。
     */
    public InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.e("liuhenpgu","InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
//                toast(getResources().getString(R.string.init_xunfei_fail2));
//                LogUtil.show("初始化语音失败，code：" + code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

  public  void playClick(String currentText) {
        if (null == mTts) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
           toast(getResources().getString(R.string.init_xunfei_fail));
            return;
        }

        switch (playStatus) {
            case 0://第一次进入时的默认状态
                if (currentText.equals("")) {
                   // toast(getResources().getString(R.string.data_init_no));
                    return;
                }
                // 设置参数
                setParam();
                int code = mTts.startSpeaking(currentText, mTtsListener);
//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
//			String path = Environment.getExternalStorageDirectory()+"/tts.ico";
//			int code = mTts.synthesizeToUri(text, path, mTtsListener);

                if (code != ErrorCode.SUCCESS) {
                   // showTip(getResources().getString(R.string.init_xunfei_fail3));
                }
                break;
            case 1://播放状态
                mTts.pauseSpeaking();
                break;
            case 2://暂停状态
                mTts.resumeSpeaking();
                break;
            default:
                break;
        }
    }

    private int playStatus;//播放状态
    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;
    /**
     * 合成回调监听。
     */
    public SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            playStatus = 1;
           // viewHolder.setImageDrawable(getResources().getDrawable(R.mipmap.iv_pause));
            poetryDetailHeaderSelection.setResource(1);
        }

        @Override
        public void onSpeakPaused() {
            playStatus = 2;
          //  viewHolder.setImageDrawable(getResources().getDrawable(R.mipmap.iv_pause));
            poetryDetailHeaderSelection.setResource(1);

        }

        @Override
        public void onSpeakResumed() {
            playStatus = 1;
           // viewHolder.setImageDrawable(getResources().getDrawable(R.mipmap.iv_pause));
            poetryDetailHeaderSelection.setResource(1);
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            mPercentForBuffering = percent;
            showTip(String.format(getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
            toast(String.format(getString(R.string.tts_toast_format), mPercentForBuffering, mPercentForPlaying));
            //showTip(String.format(getString(R.string.tts_toast_format), mPercentForBuffering, mPercentForPlaying));

            Log.e("liuhengpu",""+beginPos+"'"+endPos);
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                playStatus = 0;
               // viewHolder.setImageDrawable(getResources().getDrawable(R.mipmap.iv_pause));
                poetryDetailHeaderSelection.setResource(0);
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };
    private void showTip(String message) {
        toast(message);
    }
}
