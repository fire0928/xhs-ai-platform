import router from '@ohos.router';
import { Colors } from '../common/Theme';

@Entry
@Component
struct Settings {
  @State defaultAgent: number = 0;
  @State contentStyle: number = 0;
  @State notifPublish: boolean = true;
  @State notifReview: boolean = true;
  @State notifCookie: boolean = true;

  private agents: string[] = ['无偏好', '生活分享', '美食探店', '旅行攻略', '穿搭时尚'];
  private styles: string[] = ['自动匹配', '轻松随意', '专业严谨', '潮流时尚', '温暖治愈'];

  build() {
    Column() {
      Row() {
        Text('‹ 返回').fontSize(16).fontColor(Colors.cp500).onClick(() => { router.back() })
        Blank()
        Text('个人设置').fontSize(17).fontWeight(600).fontColor(Colors.cn900)
        Blank()
        Text('保存').fontSize(16).fontColor(Colors.cp500).fontWeight(600).onClick(() => { this.save() })
      }
      .width('100%').padding(16).backgroundColor(Color.White)

      Scroll() {
        Column({ space: 12 }) {
          // AI 偏好
          Column({ space: 0 }) {
            Text('AI 偏好').fontSize(13).fontWeight(600).fontColor(Colors.cn500).padding(16)
            Row() {
              Text('默认 Agent').fontSize(14).fontColor(Colors.cn800).layoutWeight(1)
              Select([{ value: '无偏好' }, { value: '生活分享' }, { value: '美食探店' }, { value: '旅行攻略' }, { value: '穿搭时尚' }])
                .selected(this.defaultAgent).onSelect((idx) => { this.defaultAgent = idx })
            }.width('100%').padding({ left: 16, right: 16, top: 10, bottom: 10 })
            Row() {
              Text('内容风格').fontSize(14).fontColor(Colors.cn800).layoutWeight(1)
              Select([{ value: '自动匹配' }, { value: '轻松随意' }, { value: '专业严谨' }, { value: '潮流时尚' }, { value: '温暖治愈' }])
                .selected(this.contentStyle).onSelect((idx) => { this.contentStyle = idx })
            }.width('100%').padding({ left: 16, right: 16, top: 10, bottom: 10 })
          }
          .backgroundColor(Color.White).borderRadius(12)

          // 通知设置
          Column({ space: 0 }) {
            Text('通知设置').fontSize(13).fontWeight(600).fontColor(Colors.cn500).padding(16)
            this.SettingToggle('发布结果通知', this.notifPublish, (v) => { this.notifPublish = v })
            this.SettingToggle('审核结果通知', this.notifReview, (v) => { this.notifReview = v })
            this.SettingToggle('Cookie 过期提醒', this.notifCookie, (v) => { this.notifCookie = v })
          }
          .backgroundColor(Color.White).borderRadius(12)
        }
        .padding(16)
      }
      .layoutWeight(1).scrollBar(BarState.Off)
    }
    .width('100%').height('100%').backgroundColor(Colors.bg)
  }

  @Builder SettingToggle(label: string, checked: boolean, onChange: (v: boolean) => void) {
    Row() {
      Text(label).fontSize(14).fontColor(Colors.cn800)
      Blank()
      Toggle({ type: ToggleType.Switch, isOn: checked })
        .selectedColor(Colors.cp500)
        .onChange(onChange)
    }
    .width('100%').padding({ left: 16, right: 16, top: 10, bottom: 10 })
  }

  save(): void {
    // 保存设置逻辑
    router.back()
  }
}
