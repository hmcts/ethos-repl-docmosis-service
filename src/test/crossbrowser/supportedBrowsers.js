const LATEST_MAC = 'macOS 10.15';
const LATEST_WINDOWS = 'Windows 10';

const supportedBrowsers = {
  microsoft: {
    edge: {
      browserName: 'MicrosoftEdge',
      platformName: LATEST_WINDOWS,
      browserVersion: 'latest',
      'sauce:options': {
        name: 'Win_Edge_latest'
      }
    }
  },
  safari: {
    safari_mac_latest: {
      browserName: 'safari',
      platformName: 'macOS 10.14',
      browserVersion: 'latest',
      'sauce:options': {
        name: 'Mac_Safari_latest',
        seleniumVersion: '3.141.59',
        screenResolution: '1400x1050'
      }
    }
  },
  chrome: {
    chrome_win_latest: {
      browserName: 'chrome',
      platformName: LATEST_WINDOWS,
      browserVersion: 'latest',
      'sauce:options': {
        name: 'Win_Chrome_latest'
      }
    },
    chrome_mac_latest: {
      browserName: 'chrome',
      platformName: LATEST_MAC,
      browserVersion: 'latest',
      'sauce:options': {
        name: 'Mac_Chrome_latest'
      }
    }
  },
  firefox: {
    firefox_win_latest: {
      browserName: 'firefox',
      platformName: LATEST_WINDOWS,
      browserVersion: 'latest',
      'sauce:options': {
        name: 'Win_Firefox_latest'
      }
    },
    firefox_mac_latest: {
      browserName: 'firefox',
      platformName: LATEST_MAC,
      browserVersion: 'latest',
      'sauce:options': {
        name: 'Mac_Firefox_latest'
      }
    }
  }
};

module.exports = supportedBrowsers;