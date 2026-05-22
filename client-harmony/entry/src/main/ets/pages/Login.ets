import router from '@ohos.router';
import { Colors } from '../common/Theme';
import { apiPost, setToken } from '../common/Api';

@Entry
@Component
struct Login {
  @State phone: string = '';
  @State code: string = '';
  @State codeText: string = '获取验证码';
  @State counting: boolean = false;

  build() {
    Column() {
      // 装饰背景
      Stack() {
        Circle({ width: 300, height: 300 })
          .fill(Colors.cp500).opacity(0.08)
          .position({ x: -80, y: -60 })
        Circle({ width: 240, height: 240 })
          .fill(Colors.caPurple).opacity(0.06)
          .position({ x: 180, y: 420 })
      }

      Column({ space: 20 }) {
        // Logo
        Row() {
          Text('◆').fontSize(32).fontColor(Color.White)
        }
        .width(72).height(72).borderRadius(18)
        .linearGradient({ direction: GradientDirection.RightTop, colors: [[Colors.cp500, 0], [Colors.caPurple, 1]] })
        .justifyContent(FlexAlign.Center)

        Column({ space: 4 }) {
          Text('红书智创').fontSize(28).fontWeight(FontWeight.Bold).fontColor(Color.White)
          Text('让小红书运营更轻松').fontSize(14).fontColor('#FFFFFF99')
        }

        // 表单
        Column({ space: 12 }) {
          TextInput({ placeholder: '请输入手机号' })
            .maxLength(11).type(InputType.PhoneNumber)
            .onChange((v) => { this.phone = v })
            .backgroundColor('#FFFFFF14').fontColor(Color.White)
            .placeholderColor({ resource: '#FFFFFF66' })
            .borderRadius(12).height(50).padding(16)

          Row({ space: 10 }) {
            TextInput({ placeholder: '验证码' })
              .maxLength(6).type(InputType.Number)
              .onChange((v) => { this.code = v })
              .backgroundColor('#FFFFFF14').fontColor(Color.White)
              .placeholderColor({ resource: '#FFFFFF66' })
              .borderRadius(12).height(50).padding(16).layoutWeight(1)

            Button(this.codeText)
              .type(ButtonType.Normal)
              .backgroundColor(Colors.cp500)
              .borderRadius(12).height(50).padding({ left: 16, right: 16 })
              .enabled(!this.counting)
              .onClick(() => { this.sendCode() })
          }

          Button('登录 / 注册')
            .type(ButtonType.Normal)
            .width('100%').height(56).borderRadius(12)
            .linearGradient({ direction: GradientDirection.RightTop, colors: [[Colors.cp500, 0], [Colors.cp400, 1]] })
            .shadow({ radius: 24, color: '#FE2C554D', offsetY: 8 })
            .onClick(() => { this.doLogin() })
        }
        .width('100%')

        Text('登录即表示同意 用户协议 和 隐私政策')
          .fontSize(11).fontColor('#FFFFFF59').textAlign(TextAlign.Center)
      }
      .padding(32).width('100%')
    }
    .width('100%').height('100%')
    .backgroundColor(Colors.cn900)
    .justifyContent(FlexAlign.Center)
  }

  async sendCode(): Promise<void> {
    this.counting = true;
    let sec = 60;
    this.codeText = `${sec}s`;
    let timer = setInterval(() => { sec--; this.codeText = sec > 0 ? `${sec}s` : '重新获取'; if (sec <= 0) { clearInterval(timer); this.counting = false; } }, 1000);
  }

  async doLogin(): Promise<void> {
    try {
      let res = await apiPost('/user/login', { phone: this.phone, code: this.code }) as Record<string, Object>;
      if (res['code'] === 200) {
        let data = res['data'] as Record<string, string>;
        setToken(data['token']);
        router.replaceUrl({ url: 'pages/Index' });
      }
    } catch (e) { }
  }
}
