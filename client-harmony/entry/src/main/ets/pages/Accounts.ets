import router from '@ohos.router';
import { Colors } from '../common/Theme';

@Entry
@Component
struct Accounts {
  @State accounts: AccountItem[] = [
    { name: '穿搭号', xhsId: '100301', online: true, color: Colors.cp500 },
    { name: '美食号', xhsId: '100302', online: false, color: Colors.caOrange }
  ];

  build() {
    Column() {
      // 导航栏
      Row() {
        Text('‹ 返回').fontSize(16).fontColor(Colors.cp500)
          .onClick(() => { router.back() })
        Blank()
        Text('账号管理').fontSize(17).fontWeight(600).fontColor(Colors.cn900)
        Blank()
        Text('').width(60)
      }
      .width('100%').padding(16).backgroundColor(Color.White)

      Scroll() {
        Column({ space: 12 }) {
          ForEach(this.accounts, (item: AccountItem) => {
            Column() {
              Row({ space: 10 }) {
                Row() { Text(item.name.charAt(0)).fontColor(Color.White).fontWeight(700) }
                  .width(40).height(40).borderRadius(10)
                  .linearGradient({ direction: GradientDirection.RightTop, colors: [[item.color, 0], [item.color + 'CC', 1]] })
                  .justifyContent(FlexAlign.Center)
                Column({ space: 2 }) {
                  Text(item.name).fontSize(14).fontWeight(600).fontColor(Colors.cn800)
                  Text('ID: ' + item.xhsId).fontSize(11).fontColor(Colors.cn400)
                }.alignItems(HorizontalAlign.Start).layoutWeight(1)
                Text(item.online ? '在线' : '过期')
                  .fontSize(11).fontWeight(600)
                  .fontColor(item.online ? '#16A34A' : '#B45309')
                  .backgroundColor(item.online ? '#DCFCE7' : Colors.caOrangeL)
                  .borderRadius(16).padding({ left: 10, right: 10, top: 3, bottom: 3 })
              }
              Row({ space: 8 }) {
                Button('验证').fontSize(11).height(30)
                  .backgroundColor('transparent').fontColor(Colors.cn600)
                  .border({ width: 1, color: Colors.cn200 }).borderRadius(6)
                Button('解绑').fontSize(11).height(30)
                  .backgroundColor('transparent').fontColor(Colors.ce)
                  .border({ width: 1, color: '#FECACA' }).borderRadius(6)
              }.width('100%').margin({ top: 12 })
            }
            .width('100%').padding(16).backgroundColor(Color.White).borderRadius(12)
          })

          Button('+ 添加小红书账号')
            .width('100%').height(50).borderRadius(12)
            .backgroundColor('transparent').border({ width: 1.5, color: Colors.cn300, style: BorderStyle.Dashed })
            .fontColor(Colors.cn400)
        }
        .padding(16)
      }
      .layoutWeight(1).scrollBar(BarState.Off)
    }
    .width('100%').height('100%').backgroundColor(Colors.bg)
  }
}

class AccountItem {
  name: string = '';
  xhsId: string = '';
  online: boolean = false;
  color: string = '';
}
