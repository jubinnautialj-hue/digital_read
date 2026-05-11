const KEYBOARD_COMMANDS = {
  'Space': { action: 'toggleSpeech', description: '播放/暂停朗读', scope: 'reader' },
  'Enter': { action: 'activate', description: '激活当前元素', scope: 'global' },
  'Escape': { action: 'escape', description: '取消/返回', scope: 'global' },
  'Tab': { action: 'nextFocusable', description: '下一个可聚焦元素', scope: 'global' },
  'Shift+Tab': { action: 'prevFocusable', description: '上一个可聚焦元素', scope: 'global' },
  'ArrowRight': { action: 'nextParagraph', description: '下一段', scope: 'reader' },
  'ArrowLeft': { action: 'prevParagraph', description: '上一段', scope: 'reader' },
  'ArrowUp': { action: 'increaseSpeed', description: '加快语速', scope: 'reader' },
  'ArrowDown': { action: 'decreaseSpeed', description: '减慢语速', scope: 'reader' },
  'PageUp': { action: 'prevSection', description: '上一节', scope: 'reader' },
  'PageDown': { action: 'nextSection', description: '下一节', scope: 'reader' },
  'Home': { action: 'goToStart', description: '回到开头', scope: 'reader' },
  'End': { action: 'goToEnd', description: '跳至末尾', scope: 'reader' },
  '1': { action: 'jumpToHeading', level: 1, description: '跳转至一级标题', scope: 'reader' },
  '2': { action: 'jumpToHeading', level: 2, description: '跳转至二级标题', scope: 'reader' },
  '3': { action: 'jumpToHeading', level: 3, description: '跳转至三级标题', scope: 'reader' },
  'Ctrl+ArrowRight': { action: 'nextWord', description: '下一个词', scope: 'reader' },
  'Ctrl+ArrowLeft': { action: 'prevWord', description: '上一个词', scope: 'reader' },
  'Ctrl+ArrowUp': { action: 'increasePitch', description: '提高音调', scope: 'reader' },
  'Ctrl+ArrowDown': { action: 'decreasePitch', description: '降低音调', scope: 'reader' },
  'Alt+ArrowRight': { action: 'nextHeading', description: '下一个标题', scope: 'reader' },
  'Alt+ArrowLeft': { action: 'prevHeading', description: '上一个标题', scope: 'reader' },
  'Ctrl+1': { action: 'navigateTo', target: '/user/dashboard', description: '导航到首页', scope: 'global' },
  'Ctrl+2': { action: 'navigateTo', target: '/user/documents', description: '导航到文档列表', scope: 'global' },
  'Ctrl+3': { action: 'navigateTo', target: '/user/settings', description: '导航到设置', scope: 'global' },
  'Ctrl+S': { action: 'save', description: '保存', scope: 'global' },
  'Ctrl+F': { action: 'search', description: '搜索', scope: 'global' },
  'Ctrl+H': { action: 'toggleHelp', description: '显示/隐藏帮助', scope: 'global' },
  'Ctrl+M': { action: 'toggleMenu', description: '显示/隐藏菜单', scope: 'global' },
  '?': { action: 'showCommandPalette', description: '显示命令面板', scope: 'global' },
  'G': { action: 'showNavigationMenu', description: '显示导航菜单', scope: 'global' }
}

const NAVIGATION_REGIONS = [
  { id: 'header', label: '页面头部', selector: 'header, [role="banner"]' },
  { id: 'main', label: '主要内容', selector: 'main, [role="main"], #app' },
  { id: 'nav', label: '导航区', selector: 'nav, [role="navigation"]' },
  { id: 'aside', label: '侧边栏', selector: 'aside, [role="complementary"]' },
  { id: 'search', label: '搜索区', selector: '[role="search"]' },
  { id: 'form', label: '表单区', selector: 'form, [role="form"]' },
  { id: 'dialog', label: '对话框', selector: '[role="dialog"], [role="alertdialog"]' },
  { id: 'toolbar', label: '工具栏', selector: '[role="toolbar"]' },
  { id: 'status', label: '状态栏', selector: '[role="status"]' },
  { id: 'footer', label: '页脚', selector: 'footer, [role="contentinfo"]' }
]

class KeyboardNavigation {
  constructor() {
    this.currentScope = 'global'
    this.focusableElements = []
    this.currentFocusIndex = -1
    this.commandHandlers = {}
    this.announceQueue = []
    this.isAnnouncing = false
    this.helpVisible = false
    this.commandPaletteVisible = false
    this.navigationMenuVisible = false
    this.readerContext = null
  }

  init(options = {}) {
    this.options = options
    this.bindGlobalKeys()
    this.setupLiveRegion()
    this.announce('键盘导航已启用，按问号键查看可用命令')
  }

  setReaderContext(context) {
    this.readerContext = context
    this.currentScope = 'reader'
  }

  clearReaderContext() {
    this.readerContext = null
    this.currentScope = 'global'
  }

  registerHandler(action, handler) {
    this.commandHandlers[action] = handler
  }

  bindGlobalKeys() {
    document.addEventListener('keydown', (e) => {
      const keyCombo = this.getKeyCombo(e)
      const command = KEYBOARD_COMMANDS[keyCombo]

      if (command) {
        if (command.scope === 'global' || command.scope === this.currentScope) {
          e.preventDefault()
          e.stopPropagation()
          this.executeCommand(command, e)
        }
      }
    }, true)
  }

  getKeyCombo(e) {
    const keys = []
    if (e.ctrlKey) keys.push('Ctrl')
    if (e.altKey) keys.push('Alt')
    if (e.shiftKey && !['Tab'].includes(e.key)) keys.push('Shift')
    
    let key = e.key
    if (e.key === ' ') key = 'Space'
    keys.push(key)
    
    return keys.join('+')
  }

  executeCommand(command, event) {
    const { action, description } = command
    
    if (this.commandHandlers[action]) {
      this.commandHandlers[action](command, event)
      this.announce(description)
      return
    }

    switch (action) {
      case 'escape':
        this.handleEscape()
        break
      case 'toggleHelp':
        this.toggleHelp()
        break
      case 'showCommandPalette':
        this.showCommandPalette()
        break
      case 'showNavigationMenu':
        this.showNavigationMenu()
        break
      case 'navigateTo':
        this.navigateTo(command.target)
        break
      case 'nextFocusable':
        this.focusNext()
        break
      case 'prevFocusable':
        this.focusPrev()
        break
      default:
        if (this.readerContext) {
          this.executeReaderCommand(command, event)
        }
    }
  }

  executeReaderCommand(command, event) {
    if (!this.readerContext) return

    const { action } = command
    const ctx = this.readerContext

    switch (action) {
      case 'toggleSpeech':
        if (ctx.toggleSpeech) ctx.toggleSpeech()
        break
      case 'nextParagraph':
        if (ctx.nextParagraph) ctx.nextParagraph()
        break
      case 'prevParagraph':
        if (ctx.prevParagraph) ctx.prevParagraph()
        break
      case 'increaseSpeed':
        if (ctx.increaseSpeed) ctx.increaseSpeed()
        break
      case 'decreaseSpeed':
        if (ctx.decreaseSpeed) ctx.decreaseSpeed()
        break
      case 'increasePitch':
        if (ctx.increasePitch) ctx.increasePitch()
        break
      case 'decreasePitch':
        if (ctx.decreasePitch) ctx.decreasePitch()
        break
      case 'nextSection':
        if (ctx.nextSection) ctx.nextSection()
        break
      case 'prevSection':
        if (ctx.prevSection) ctx.prevSection()
        break
      case 'nextHeading':
        if (ctx.nextHeading) ctx.nextHeading()
        break
      case 'prevHeading':
        if (ctx.prevHeading) ctx.prevHeading()
        break
      case 'jumpToHeading':
        if (ctx.jumpToHeading) ctx.jumpToHeading(command.level)
        break
      case 'goToStart':
        if (ctx.goToStart) ctx.goToStart()
        break
      case 'goToEnd':
        if (ctx.goToEnd) ctx.goToEnd()
        break
    }
  }

  handleEscape() {
    if (this.commandPaletteVisible) {
      this.hideCommandPalette()
    } else if (this.navigationMenuVisible) {
      this.hideNavigationMenu()
    } else if (this.helpVisible) {
      this.hideHelp()
    } else {
      if (this.readerContext && this.readerContext.goBack) {
        this.readerContext.goBack()
      }
    }
  }

  toggleHelp() {
    if (this.helpVisible) {
      this.hideHelp()
    } else {
      this.showHelp()
    }
  }

  showHelp() {
    this.helpVisible = true
    this.announce('显示帮助信息')
    this.createHelpOverlay()
  }

  hideHelp() {
    this.helpVisible = false
    this.announce('隐藏帮助信息')
    this.removeHelpOverlay()
  }

  createHelpOverlay() {
    const existing = document.getElementById('keyboard-help-overlay')
    if (existing) return

    const overlay = document.createElement('div')
    overlay.id = 'keyboard-help-overlay'
    overlay.setAttribute('role', 'dialog')
    overlay.setAttribute('aria-label', '键盘快捷键帮助')
    overlay.style.cssText = `
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.8);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 99999;
    `

    const content = document.createElement('div')
    content.style.cssText = `
      background: white;
      padding: 24px;
      border-radius: 8px;
      max-width: 600px;
      max-height: 80vh;
      overflow-y: auto;
    `

    const title = document.createElement('h2')
    title.textContent = '键盘快捷键'
    title.style.cssText = 'margin: 0 0 16px 0;'
    content.appendChild(title)

    const globalSection = this.createSection('全局命令', 
      Object.entries(KEYBOARD_COMMANDS).filter(([, c]) => c.scope === 'global')
    )
    content.appendChild(globalSection)

    const readerSection = this.createSection('阅读器命令',
      Object.entries(KEYBOARD_COMMANDS).filter(([, c]) => c.scope === 'reader')
    )
    content.appendChild(readerSection)

    const closeHint = document.createElement('p')
    closeHint.textContent = '按 Escape 关闭此帮助'
    closeHint.style.cssText = 'margin-top: 16px; color: #666; font-style: italic;'
    content.appendChild(closeHint)

    overlay.appendChild(content)
    document.body.appendChild(overlay)
  }

  createSection(title, commands) {
    const section = document.createElement('div')
    section.style.marginBottom = '16px'

    const h3 = document.createElement('h3')
    h3.textContent = title
    h3.style.cssText = 'margin: 0 0 8px 0; font-size: 16px;'
    section.appendChild(h3)

    const list = document.createElement('ul')
    list.style.cssText = 'list-style: none; padding: 0; margin: 0;'

    commands.forEach(([key, command]) => {
      const li = document.createElement('li')
      li.style.cssText = 'display: flex; justify-content: space-between; padding: 4px 0; border-bottom: 1px solid #eee;'
      
      const keySpan = document.createElement('span')
      keySpan.style.cssText = 'font-family: monospace; background: #f0f0f0; padding: 2px 8px; border-radius: 4px;'
      keySpan.textContent = key
      
      const descSpan = document.createElement('span')
      descSpan.style.cssText = 'color: #333;'
      descSpan.textContent = command.description
      
      li.appendChild(keySpan)
      li.appendChild(descSpan)
      list.appendChild(li)
    })

    section.appendChild(list)
    return section
  }

  removeHelpOverlay() {
    const overlay = document.getElementById('keyboard-help-overlay')
    if (overlay) {
      overlay.remove()
    }
  }

  showCommandPalette() {
    this.commandPaletteVisible = true
    this.announce('显示命令面板')
    this.createCommandPalette()
  }

  hideCommandPalette() {
    this.commandPaletteVisible = false
    this.removeCommandPalette()
  }

  createCommandPalette() {
    const existing = document.getElementById('command-palette')
    if (existing) return

    const palette = document.createElement('div')
    palette.id = 'command-palette'
    palette.setAttribute('role', 'dialog')
    palette.setAttribute('aria-label', '命令面板')
    palette.style.cssText = `
      position: fixed;
      top: 20%;
      left: 50%;
      transform: translateX(-50%);
      background: white;
      padding: 16px;
      border-radius: 8px;
      box-shadow: 0 10px 40px rgba(0,0,0,0.3);
      z-index: 100000;
      min-width: 400px;
    `

    const input = document.createElement('input')
    input.type = 'text'
    input.placeholder = '输入命令...'
    input.setAttribute('aria-label', '搜索命令')
    input.style.cssText = `
      width: 100%;
      padding: 12px;
      font-size: 16px;
      border: 2px solid #1890ff;
      border-radius: 4px;
      box-sizing: border-box;
    `

    const list = document.createElement('div')
    list.id = 'command-list'
    list.style.cssText = 'max-height: 300px; overflow-y: auto; margin-top: 8px;'

    const renderCommands = (filter = '') => {
      list.innerHTML = ''
      Object.entries(KEYBOARD_COMMANDS)
        .filter(([, cmd]) => 
          cmd.description.toLowerCase().includes(filter.toLowerCase()) ||
          cmd.action.toLowerCase().includes(filter.toLowerCase())
        )
        .forEach(([key, cmd]) => {
          const item = document.createElement('div')
          item.style.cssText = 'padding: 8px 12px; cursor: pointer; display: flex; justify-content: space-between;'
          item.innerHTML = `
            <span>${cmd.description}</span>
            <span style="font-family: monospace; background: #f0f0f0; padding: 2px 6px; border-radius: 4px;">${key}</span>
          `
          list.appendChild(item)
        })
    }

    input.addEventListener('input', (e) => renderCommands(e.target.value))
    input.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') {
        e.stopPropagation()
        this.hideCommandPalette()
      }
    })

    palette.appendChild(input)
    palette.appendChild(list)
    document.body.appendChild(palette)
    input.focus()
    renderCommands()
  }

  removeCommandPalette() {
    const palette = document.getElementById('command-palette')
    if (palette) {
      palette.remove()
    }
  }

  showNavigationMenu() {
    this.navigationMenuVisible = true
    this.announce('显示导航菜单')
    this.createNavigationMenu()
  }

  hideNavigationMenu() {
    this.navigationMenuVisible = false
    this.removeNavigationMenu()
  }

  createNavigationMenu() {
    const existing = document.getElementById('navigation-menu')
    if (existing) return

    const menu = document.createElement('div')
    menu.id = 'navigation-menu'
    menu.setAttribute('role', 'dialog')
    menu.setAttribute('aria-label', '区域导航')
    menu.style.cssText = `
      position: fixed;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      background: white;
      padding: 16px;
      border-radius: 8px;
      box-shadow: 0 10px 40px rgba(0,0,0,0.3);
      z-index: 100000;
      min-width: 300px;
    `

    const title = document.createElement('h3')
    title.textContent = '跳转到区域'
    title.style.cssText = 'margin: 0 0 12px 0;'
    menu.appendChild(title)

    NAVIGATION_REGIONS.forEach((region, index) => {
      const element = document.querySelector(region.selector)
      if (element) {
        const item = document.createElement('div')
        item.style.cssText = 'padding: 8px 12px; cursor: pointer; border-bottom: 1px solid #eee;'
        item.innerHTML = `<strong>${index + 1}.</strong> ${region.label}`
        item.addEventListener('click', () => {
          this.focusRegion(region)
          this.hideNavigationMenu()
        })
        menu.appendChild(item)
      }
    })

    const hint = document.createElement('p')
    hint.textContent = '按 Escape 关闭'
    hint.style.cssText = 'margin-top: 12px; color: #666; font-size: 12px;'
    menu.appendChild(hint)

    document.body.appendChild(menu)
  }

  removeNavigationMenu() {
    const menu = document.getElementById('navigation-menu')
    if (menu) {
      menu.remove()
    }
  }

  focusRegion(region) {
    const element = document.querySelector(region.selector)
    if (element) {
      element.setAttribute('tabindex', '-1')
      element.focus()
      element.scrollIntoView({ behavior: 'smooth', block: 'start' })
      this.announce(`已跳转到${region.label}`)
    }
  }

  navigateTo(target) {
    if (this.commandHandlers['navigateTo']) {
      this.commandHandlers['navigateTo']({ target })
    }
  }

  focusNext() {
    const focusables = this.getFocusableElements()
    if (focusables.length === 0) return

    const currentIndex = focusables.indexOf(document.activeElement)
    const nextIndex = (currentIndex + 1) % focusables.length
    focusables[nextIndex].focus()
  }

  focusPrev() {
    const focusables = this.getFocusableElements()
    if (focusables.length === 0) return

    const currentIndex = focusables.indexOf(document.activeElement)
    const prevIndex = currentIndex <= 0 ? focusables.length - 1 : currentIndex - 1
    focusables[prevIndex].focus()
  }

  getFocusableElements() {
    const selectors = [
      'a[href]',
      'area[href]',
      'input:not([disabled])',
      'select:not([disabled])',
      'textarea:not([disabled])',
      'button:not([disabled])',
      'iframe',
      '[tabindex]:not([tabindex="-1"])',
      '[contenteditable="true"]'
    ]
    return Array.from(document.querySelectorAll(selectors.join(',')))
      .filter(el => !el.hasAttribute('disabled') && el.offsetParent !== null)
  }

  setupLiveRegion() {
    let liveRegion = document.getElementById('accessibility-live-region')
    if (!liveRegion) {
      liveRegion = document.createElement('div')
      liveRegion.id = 'accessibility-live-region'
      liveRegion.setAttribute('aria-live', 'polite')
      liveRegion.setAttribute('aria-atomic', 'true')
      liveRegion.style.cssText = `
        position: absolute;
        left: -10000px;
        top: auto;
        width: 1px;
        height: 1px;
        overflow: hidden;
      `
      document.body.appendChild(liveRegion)
    }
    this.liveRegion = liveRegion
  }

  announce(message) {
    this.announceQueue.push(message)
    if (!this.isAnnouncing) {
      this.processAnnounceQueue()
    }
  }

  processAnnounceQueue() {
    if (this.announceQueue.length === 0) {
      this.isAnnouncing = false
      return
    }

    this.isAnnouncing = true
    const message = this.announceQueue.shift()

    if (this.liveRegion) {
      this.liveRegion.textContent = ''
      setTimeout(() => {
        this.liveRegion.textContent = message
      }, 50)
    }

    setTimeout(() => {
      this.processAnnounceQueue()
    }, 500)
  }

  destroy() {
    this.removeHelpOverlay()
    this.removeCommandPalette()
    this.removeNavigationMenu()
    if (this.liveRegion) {
      this.liveRegion.remove()
    }
  }

  getAvailableCommands() {
    return KEYBOARD_COMMANDS
  }

  getNavigationRegions() {
    return NAVIGATION_REGIONS
  }
}

export const keyboardNav = new KeyboardNavigation()
export default keyboardNav
