import styled from "styled-components";

/**
 * <Button
 *   style='danger' />
 * <ButtonDanger>
 * @type {StyledComponent<"button", AnyIfEmpty<DefaultTheme>, {}, never>}
 */

export const Button = styled.button`
  min-width: 150px;
  color: #ffffff;
  box-shadow: 0 2px 5px 0 rgba(0,0,0,.26);
  background-color: ${props => props.theme.colors.blue5};
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  min-height: 36px;
  text-align: center;
  border-radius: 3px;
  border: 0;
  margin: 6px 16px;
  padding: 0 6px;
  :active {
    transform: translateY(2px);
  }
`