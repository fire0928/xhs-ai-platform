import router from '@ohos.router';
import { Colors } from '../common/Theme';

@Entry
@Component
struct Index {
  @State currentTab: number = 0;

  build() {
    Column() {
      // 状态栏
      Row() {
        Text('9:41').fontSize(13).fontWeight(600).fontColor(Colors.cn900)
        Blank()
        Row({ space: 2 }) {
          Rect().width(3).height(4).fill(Colors.cn900).borderRadius(1)
          Rect().width(3).height(7).fill(Colors.cn900).borderRadius(1)
          Rect().width(3).height(9).fill(Colors.cn900).borderRadius(1)
          Rect().width(3).height(12).fill(Colors.cn900).borderRadius(1).opacity(0.3)
        }
      }
      .width('100%').height(44).padding({ left: 24, right: 24, bottom: 4 })

      // 内容区
      if (this.currentTab === 0) { this.HomePage() }
      if (this.currentTab === 1) { this.CreatePage() }
      if (this.currentTab === 2) { this.ReviewPage() }
      if (this.currentTab === 3) { this.PublishPage() }
      if (this.currentTab === 4) { this.ProfilePage() }

      // 底部 Tab 栏
      Row() {
        ForEach(['首页', 'AI创作', '审核', '发布', '我的'], (label: string, idx: number) => {
          Column({ space: 2 }) {
            Text(this.tabIcon(idx)).fontSize(20)
            Text(label).fontSize(10).fontWeight(idx === this.currentTab ? 600 : 400)
          }
          .width('20%').height(50)
          .fontColor(idx === this.currentTab ? Colors.cp500 : Colors.cn400)
          .justifyContent(FlexAlign.Center)
          .onClick(() => { this.currentTab = idx })
        })
      }
      .width('100%').height(50)
      .backgroundColor(Color.White)
      .border({ width: { top: 1 }, color: Colors.cn200 })
    }
    .width('100%').height('100%').backgroundColor(Colors.bg)
  }

  tabIcon(idx: number): string {
    return ['🏠', '✨', '✅', '📨', '👤'][idx] ?? '?';
  }

  @Builder HomePage() {
    Scroll() {
      Column({ space: 12 }) {
        // 欢迎区
        Row({ space: 12 }) {
          Row() { Text('创').fontColor(Color.White).fontWeight(700) }
            .width(40).height(40).borderRadius(20)
            .linearGradient({ direction: GradientDirection.RightTop, colors: [[Colors.cp500, 0], [Colors.cp400, 1]] })
            .justifyContent(FlexAlign.Center)
          Column({ space: 2 }) {
            Text('早上好，创作者').fontSize(15).fontWeight(700).fontColor(Colors.cn900)
            Text('今天有 3 篇内容待发布').fontSize(11).fontColor(Colors.cn500)
          }.alignItems(HorizontalAlign.Start)
          Blank()
          Text('3').fontSize(11).fontWeight(600).fontColor(Colors.cp600)
            .backgroundColor(Colors.cp50).borderRadius(10).padding({ left: 8, right: 8, top: 3, bottom: 3 })
        }
        .width('100%').padding(16).backgroundColor(Color.White)

        // 指标卡片
        Grid() {
          GridItem() { this.MetricCard('今日发布', '4', '↑ +12%', true) }
          GridItem() { this.MetricCard('互动量', '2.8k', '↑ +8%', true) }
          GridItem() { this.MetricCard('粉丝增长', '+156', '↑ +23%', true) }
          GridItem() { this.MetricCard('AI创作', '12', '次', false) }
        }
        .columnsTemplate('1fr 1fr').rowsGap(10).columnsGap(10)
        .padding({ left: 16, right: 16 })

        // 图表占位
        Row() {
          Text('近7天趋势').fontSize(13).fontWeight(600).fontColor(Colors.cn700)
        }
        .width('100%').padding(16).backgroundColor(Color.White).borderRadius(12)
        .margin({ left: 16, right: 16 })

        // 操作按钮
        Row({ space: 10 }) {
          Button('AI一键创作')
            .linearGradient({ direction: GradientDirection.RightTop, colors: [[Colors.cp500, 0], [Colors.cp400, 1]] })
            .borderRadius(24).height(50).layoutWeight(1)
            .fontColor(Color.White).fontWeight(600)
            .shadow({ radius: 12, color: '#FE2C554D', offsetY: 4 })
            .onClick(() => { this.currentTab = 1 })
          Button('查看队列')
            .backgroundColor(Color.White).border({ width: 1.5, color: Colors.cp500 })
            .borderRadius(24).height(50).layoutWeight(1)
            .fontColor(Colors.cp500).fontWeight(600)
            .onClick(() => { this.currentTab = 3 })
        }
        .padding({ left: 16, right: 16 })

        // 最近创作
        Row({ space: 10 }) {
          this.RecentCard('生活分享', '#FFE0E5', '周末探店｜宝藏咖啡', '已发布', '#DCFCE7', '#16A34A')
          this.RecentCard('穿搭博主', '#FEF3C7', '春日穿搭｜温柔通勤', '待发布', '#DBEAFE', '#2563EB')
          this.RecentCard('旅行攻略', '#EDE9FE', '3天2夜云南攻略', '审核中', '#FEF3C7', '#B45309')
        }
        .padding({ left: 16, right: 16, bottom: 24 })
      }
    }
    .layoutWeight(1).scrollBar(BarState.Off)
  }

  @Builder MetricCard(label: string, value: string, trend: string, up: boolean) {
    Column({ space: 4 }) {
      Text(label).fontSize(11).fontColor(Colors.cn500)
      Text(value).fontSize(24).fontWeight(700).fontColor(Colors.cn900).fontFamily('monospace')
      Text(trend).fontSize(11).fontColor(up ? Colors.cs : Colors.cn500)
    }
    .padding({ top: 14, left: 16, right: 16, bottom: 14 })
    .backgroundColor(Color.White).borderRadius(12)
    .border({ width: { top: 3 }, color: Colors.cp500 })
    .alignItems(HorizontalAlign.Start)
  }

  @Builder RecentCard(tag: string, bg: string, title: string, status: string, sbg: string, sc: string) {
    Column() {
      Row() { Text(tag).fontSize(12).fontColor(Colors.cn600).fontWeight(600) }
        .width(160).height(90).backgroundColor(bg).borderRadius({ topLeft: 10, topRight: 10 })
        .justifyContent(FlexAlign.Center)
      Column({ space: 4 }) {
        Text(title).fontSize(11).fontWeight(600).fontColor(Colors.cn700).maxLines(1).textOverflow({ overflow: TextOverflow.Ellipsis })
        Text(status).fontSize(9).fontWeight(500).fontColor(sc).backgroundColor(sbg).borderRadius(16).padding({ left: 6, right: 6, top: 2, bottom: 2 })
      }
      .padding(10).alignItems(HorizontalAlign.Start).width('100%')
    }
    .backgroundColor(Color.White).borderRadius(10).width(160)
    .shadow({ radius: 3, color: '#0000000D', offsetY: 1 })
  }

  @Builder CreatePage() {
    Scroll() {
      Column({ space: 16 }) {
        // 步骤指示器
        Row() {
          ForEach(['选择Agent', '输入主题', '确认标题', '生成内容'], (label: string, idx: number) => {
            Row() {
              Row() { Text(`${idx+1}`).fontSize(11).fontWeight(700) }
                .width(28).height(28).borderRadius(14)
                .backgroundColor(idx === 2 ? Colors.cp500 : idx < 2 ? Colors.cs : Colors.cn200)
                .fontColor(idx <= 2 ? Color.White : Colors.cn400)
                .justifyContent(FlexAlign.Center)
              if (idx < 3) {
                Rect().width(40).height(3).fill(idx < 2 ? Colors.cs : Colors.cn200).borderRadius(2)
              }
            }
          })
        }.padding(16).justifyContent(FlexAlign.Center)

        Text('③ 标题确认').fontSize(11).fontColor(Colors.cn500).textAlign(TextAlign.Center)

        // 已选 Agent
        Row({ space: 8 }) {
          Row() { }.width(32).height(32).borderRadius(8).backgroundColor(Colors.cp100)
          Column({ space: 2 }) {
            Text('生活分享 Agent').fontSize(13).fontWeight(600).fontColor(Colors.cn800)
            Text('日常 / 好物 / 探店').fontSize(10).fontColor(Colors.cn500)
          }.alignItems(HorizontalAlign.Start)
        }
        .width('100%').padding(10).borderRadius(10)
        .backgroundColor(Colors.cp50).border({ width: 1, color: Colors.cp200 })

        // 标题选项
        ForEach(['周末探店｜这家隐藏的宝藏咖啡店你必须知道！', '城市角落里的温暖时光，一杯咖啡治愈周末', '咖啡控必打卡！这家小店绝了'], (t: string, idx: number) => {
          Row({ space: 10 }) {
            Row() { Text(`${idx+1}`).fontSize(11).fontWeight(700).fontColor(idx===0?Color.White:Colors.cn500) }
              .width(24).height(24).borderRadius(12)
              .backgroundColor(idx===0?Colors.cp500:Colors.cn100)
              .justifyContent(FlexAlign.Center)
            Text(t).fontSize(13).fontColor(Colors.cn800).lineHeight(20).layoutWeight(1)
            Button('选用').fontSize(11).fontWeight(600)
              .backgroundColor(idx===0?Colors.cp500:'transparent')
              .fontColor(idx===0?Color.White:Colors.cp500)
              .border({ width: 1, color: Colors.cp500 }).borderRadius(14)
              .height(30).padding({ left: 10, right: 10 })
          }
          .width('100%').padding(12).borderRadius(10)
          .backgroundColor(idx===0 ? Colors.cp50 : Color.White)
          .border({ width: 1.5, color: idx===0 ? Colors.cp500 : Colors.cn200 })
        })

        // AI 生成预览
        Column() {
          Text('周末的午后，阳光透过玻璃窗洒进这间藏在巷子深处的咖啡店。手冲的耶加雪菲散发着花香，搭配一块柠檬塔，就是完美的下午时光...')
            .fontSize(13).fontColor(Colors.cn700).lineHeight(22)
        }
        .padding(14).borderRadius(12)
        .linearGradient({ direction: GradientDirection.RightTop, colors: [[Colors.cp100, 0], [Colors.caPurpleL, 1]] })

        // 配图
        Row({ space: 8 }) {
          ForEach([Colors.cp100, Colors.caOrangeL, Colors.caPurpleL, Colors.cn200], (bg: string) => {
            Row().width(100).height(100).backgroundColor(bg).borderRadius(10)
          })
        }

        // 底部按钮
        Row({ space: 10 }) {
          Button('修改文案').backgroundColor('transparent').fontColor(Colors.cn600).layoutWeight(1)
          Button('加入发布队列')
            .linearGradient({ direction: GradientDirection.RightTop, colors: [[Colors.cp500, 0], [Colors.caPurple, 1]] })
            .fontColor(Color.White).layoutWeight(1).borderRadius(24)
            .shadow({ radius: 12, color: '#FE2C5540', offsetY: 4 })
        }
        .width('100%').padding({ top: 12, bottom: 12 })
      }
      .padding(16)
    }
    .layoutWeight(1).scrollBar(BarState.Off)
  }

  @Builder ReviewPage() {
    Scroll() {
      Column({ space: 12 }) {
        // Tab
        Row({ space: 0 }) {
          ForEach(['全部', '待审核', '已通过', '已拒绝'], (label: string, idx: number) => {
            Text(label).fontSize(13).fontWeight(idx===0?600:400)
              .fontColor(idx===0?Colors.cp500:Colors.cn500)
              .padding({ top: 10, bottom: 10, left: 14, right: 14 })
              .border({ width: { bottom: idx===0 ? 2 : 0 }, color: Colors.cp500 })
          })
        }
        .width('100%').backgroundColor(Color.White).border({ width: { bottom: 1 }, color: Colors.cn100 })

        // 审核卡片
        ForEach([
          { title: '周末探店｜这家隐藏的宝藏咖啡店你必须知道！', tags: ['探店','咖啡','周末'], time: 'AI生成 · 2分钟前', bg: Colors.cp100 },
          { title: '春日穿搭分享｜通勤也能很温柔', tags: ['穿搭','通勤','春季'], time: 'AI生成 · 1小时前', bg: Colors.caOrangeL },
          { title: '3天2夜云南攻略｜人均2k玩转大理', tags: ['旅行','攻略','云南'], time: '手动 · 3小时前', bg: Colors.caPurpleL }
        ], (item: Record<string, Object>) => {
          Column() {
            Row() { Text(item['title'] as string).fontSize(12).fontColor(Colors.cp700).fontWeight(600) }
              .width('100%').height(180).backgroundColor(item['bg'] as string)
              .borderRadius({ topLeft: 12, topRight: 12 }).justifyContent(FlexAlign.Center)
            Column({ space: 6 }) {
              Text(item['title'] as string).fontSize(14).fontWeight(600).fontColor(Colors.cn800).maxLines(2)
              Row({ space: 4 }) {
                ForEach(item['tags'] as string[], (t: string) => {
                  Text(t).fontSize(11).fontWeight(500).fontColor(Colors.cp600)
                    .backgroundColor(Colors.cp50).borderRadius(16).padding({ left: 8, right: 8, top: 4, bottom: 4 })
                })
              }
              Text(item['time'] as string).fontSize(11).fontColor(Colors.cn500)
            }
            .padding(14).alignItems(HorizontalAlign.Start).width('100%')
          }
          .backgroundColor(Color.White).borderRadius(12).width('100%')
          .shadow({ radius: 3, color: '#0000000D', offsetY: 1 })
        })
      }
      .padding(16)
    }
    .layoutWeight(1).scrollBar(BarState.Off)
  }

  @Builder PublishPage() {
    Scroll() {
      Column({ space: 12 }) {
        // 日期选择
        Row({ space: 16 }) {
          Text('‹').fontSize(22).fontColor(Colors.cn400)
          Text('5月22日 周五').fontSize(16).fontWeight(600).fontColor(Colors.cn800)
          Text('›').fontSize(22).fontColor(Colors.cn400)
        }.justifyContent(FlexAlign.Center).padding(14)

        Text('今天').fontSize(11).fontWeight(600).fontColor(Colors.cn500).padding({ left: 4 }).width('100%')

        ForEach([
          { title: '周末探店｜宝藏咖啡店', time: '14:00 定时发布', status: '定时中', sc: '#DBEAFE', tc: '#2563EB', bg: Colors.cp100 },
          { title: '春日穿搭分享｜温柔通勤', time: '18:30 定时发布', status: '待发布', sc: Colors.caOrangeL, tc: '#B45309', bg: Colors.caOrangeL }
        ], (item: Record<string, string>) => {
          Row({ space: 12 }) {
            Row().width(56).height(56).backgroundColor(item['bg']).borderRadius(8)
            Column({ space: 2 }) {
              Text(item['title']).fontSize(13).fontWeight(600).fontColor(Colors.cn800)
              Text(item['time']).fontSize(11).fontColor(Colors.cn500)
            }.alignItems(HorizontalAlign.Start).layoutWeight(1)
            Text(item['status']).fontSize(10).fontWeight(500).fontColor(item['tc'])
              .backgroundColor(item['sc']).borderRadius(16).padding({ left: 8, right: 8, top: 3, bottom: 3 })
          }
          .width('100%').padding(12).backgroundColor(Color.White).borderRadius(10)
          .shadow({ radius: 2, color: '#0000000A', offsetY: 1 })
        })

        // 统计
        Column({ space: 8 }) {
          Row() { Text('今日待发布').fontSize(13).fontColor(Colors.cn500); Blank(); Text('3').fontSize(16).fontWeight(700).fontColor(Colors.cn900).fontFamily('monospace') }
          Row() { Text('本周计划').fontSize(13).fontColor(Colors.cn500); Blank(); Text('12').fontSize(16).fontWeight(700).fontColor(Colors.cn900).fontFamily('monospace') }
          Row() { Text('发布成功率').fontSize(13).fontColor(Colors.cn500); Blank(); Text('94%').fontWeight(600).fontColor(Colors.cs) }
          Row() { Rect().width('94%').height(6).fill(Colors.cs).borderRadius(3) }.width('100%').height(6).backgroundColor(Colors.cn100).borderRadius(3)
        }
        .width('100%').padding(14).backgroundColor(Color.White).borderRadius(10)
      }
      .padding(16)
    }
    .layoutWeight(1).scrollBar(BarState.Off)
  }

  @Builder ProfilePage() {
    Scroll() {
      Column({ space: 8 }) {
        // 头像区
        Column({ space: 10 }) {
          Row() { Text('王').fontColor(Color.White).fontSize(24).fontWeight(700) }
            .width(64).height(64).borderRadius(32)
            .linearGradient({ direction: GradientDirection.RightTop, colors: [[Colors.cp500, 0], [Colors.cp400, 1]] })
            .justifyContent(FlexAlign.Center)
          Text('创作者小王').fontSize(18).fontWeight(700).fontColor(Colors.cn900)
          Text('已绑定 2 个小红书账号').fontSize(11).fontColor(Colors.cn500)
        }
        .width('100%').padding(24).backgroundColor(Color.White)

        // 菜单分组
        Column() {
          this.ProfileItem('⭐', Colors.cp50, '账号管理', '2个账号', () => { router.pushUrl({ url: 'pages/Accounts' }) })
          this.ProfileItem('📊', '#DBEAFE', '数据分析', '', () => {})
          this.ProfileItem('🔔', Colors.caOrangeL, '通知设置', '', () => { router.pushUrl({ url: 'pages/Settings' }) })
          this.ProfileItem('✨', Colors.caPurpleL, 'AI偏好设置', '', () => {})
        }
        .backgroundColor(Color.White)

        Column() {
          this.ProfileItem('💬', '#DCFCE7', '分享给朋友', '', () => {})
          this.ProfileItem('❓', Colors.cn100, '帮助与反馈', '', () => {})
          this.ProfileItem('ℹ️', Colors.cn100, '关于我们', '', () => {})
        }
        .backgroundColor(Color.White)

        // 退出
        Button('退出登录')
          .width('100%').height(50).borderRadius(12)
          .backgroundColor('transparent').border({ width: 1.5, color: Colors.cp500 })
          .fontColor(Colors.cp500).fontWeight(600)
          .margin({ top: 24, left: 16, right: 16 })
          .onClick(() => {
            router.replaceUrl({ url: 'pages/Login' });
          })
      }
      .padding({ bottom: 24 })
    }
    .layoutWeight(1).scrollBar(BarState.Off).backgroundColor(Colors.bg)
  }

  @Builder ProfileItem(icon: string, bg: string, label: string, badge: string, action: () => void) {
    Row({ space: 12 }) {
      Row() { Text(icon).fontSize(14) }
        .width(32).height(32).borderRadius(8).backgroundColor(bg).justifyContent(FlexAlign.Center)
      Text(label).fontSize(14).fontColor(Colors.cn800).layoutWeight(1)
      if (badge) { Text(badge).fontSize(11).fontColor(Colors.cn400).margin({ right: 8 }) }
      Text('›').fontSize(16).fontColor(Colors.cn300)
    }
    .width('100%').padding({ left: 16, right: 16, top: 14, bottom: 14 })
    .onClick(action)
  }
}
