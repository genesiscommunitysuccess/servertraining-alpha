import {css} from '@microsoft/fast-element';
import {mixinScreen} from '../../styles';

export const HomeStyles = css`
  :host {
    align-items: center;
    justify-content: center;
    flex-direction: column;
    --neutral-stroke-divider-rest: var(--neutral-fill-stealth-rest);
  }

  .split-layout {
      flex-direction: column;
      flex: 1;
      width: 100%;
      height: 100%;
  }

  .top-layout {
  height: 45%;
  flex-direction: row;
  }

  .positions-card {
  flex: 1;
  margin: calc(var(--design-unit) * 3px);
  }

  .card-title {
  padding: calc(var(--design-unit) * 3px);
  background-color: #22272a;
  font-size: 13px;
  font-weight: bold;
  }

  zero-ag-grid {
  width: 100%;
  height: 100%;
  }
`;
